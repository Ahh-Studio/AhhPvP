package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.items.ModItems;
import net.minecraft.world.entity.LivingEntity;

/**
 * 杀手的统一战斗决策器（方案B）。
 * <p>
 * 原来 4~10 格内 MeleeAttackGoal 因同优先级+先注册恒占 MOVE/LOOK 标志，导致飞刀等远程
 * 永远无法触发（"死区"）。本管理器作为<b>唯一决策者</b>，每个 tick 计算当前应使用的攻击动作；
 * 各攻击 Goal 退化为薄门控（仅当动作匹配才激活）。由于每次恰好返回一种动作，
 * 同一时刻只有<b>一个</b>攻击 Goal 激活，从架构上消除同优先级竞争。
 */
public class MurdererCombatManager {

    /** 当前应执行的攻击动作 */
    public enum AttackAction {
        NONE, MELEE, DAGGER, FIREBALL, FIREBALL_JUMP, ENDER_PEARL, STUCK_MINING
    }

    // ---- 距离/高度/概率常量（集中管理，避免魔法数字散布） ----

    /** 近战最大距离平方（3.5 格） */
    public static final double MELEE_RANGE_SQ = 12.25;
    /** 近战最大垂直高度差 */
    public static final double MELEE_VERTICAL_DIFF = 2.5;
    /** 飞刀最小距离平方（3 格） */
    public static final double DAGGER_MIN_SQ = 9.0;
    /** 飞刀最大距离平方（20 格） */
    public static final double DAGGER_MAX_SQ = 400.0;
    /** 烈焰弹最小有效射程平方（12 格），低于此禁止（避免炸到自己/无意义） */
    public static final double FIREBALL_MIN_SQ = 144.0;
    /** 纯烈焰弹区（20 格）：超过此距离直接发烈焰弹 */
    public static final double FIREBALL_LONG_SQ = 400.0;
    /** 烈焰弹跳跃推进最小距离平方（12 格起，作为主要进近手段） */
    public static final double JUMP_MIN_SQ = 144.0;
    /** 末影珍珠最小距离平方（25 格） */
    public static final double PEARL_MIN_SQ = 625.0;
    /** 末影珍珠触发的垂直高度差阈值 */
    public static final double PEARL_VERTICAL_DIFF = 3.0;
    /** FireballJump 概率触发几率（原 8%/15% → 35%，作为主要进近手段） */
    public static final float FIREBALL_JUMP_CHANCE = 0.35F;
    /** 连击超过该值触发烈焰弹大招（原 >3 → 3 次即可，即 >2） */
    public static final int COMBO_FIREBALL_TRIGGER = 2;
    /** 通用追击速度：远距离烈焰弹推进与飞刀追击共用，保证各距离追击速度一致 */
    public static final double CHASE_SPEED = 1.2;

    private final MurdererEntity mob;
    private AttackAction current = AttackAction.NONE;

    public MurdererCombatManager(MurdererEntity mob) {
        this.mob = mob;
    }

    /** 每个逻辑 tick 由 {@code MurdererEntity.tick()} 调用，重新决策当前动作。 */
    public void update() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive() || this.mob.isEatingGoldenApple()) {
            this.current = AttackAction.NONE;
            return;
        }

        double distSq = target.distanceToSqr(this.mob);
        double vertDiff = Math.abs(this.mob.getY() - target.getY());
        boolean sight = this.mob.hasLineOfSight(target);

        // 1. 破墙：长时间无视线且够不到近战
        if (!sight && distSq > MELEE_RANGE_SQ) {
            this.current = AttackAction.STUCK_MINING;
            return;
        }

        boolean pearlReady = this.mob.enderPearlCooldownTicks <= 0 && !this.mob.isUnderWater();
        // 2. 末影珍珠：远距离或垂直差异过大
        if (pearlReady && (distSq > PEARL_MIN_SQ || vertDiff > PEARL_VERTICAL_DIFF)) {
            this.current = AttackAction.ENDER_PEARL;
            return;
        }

        boolean fireballReady = this.mob.fireballCooldownTicks <= 0;
        // 3. 烈焰弹跳跃推进（12 格以上、在地面、跳跃冷却就绪、概率触发；不依赖定向烈焰弹冷却）
        if (this.mob.onGround() && this.mob.fireballJumpCooldownTicks <= 0 && distSq > JUMP_MIN_SQ
                && this.mob.getRandom().nextFloat() < FIREBALL_JUMP_CHANCE) {
            this.current = AttackAction.FIREBALL_JUMP;
            return;
        }
        // 4. 烈焰弹：>=12 格且（被连击触发大招 或 超过 20 格）
        if (fireballReady && distSq >= FIREBALL_MIN_SQ
                && (this.mob.comboHitsTaken > COMBO_FIREBALL_TRIGGER || distSq > FIREBALL_LONG_SQ)) {
            this.current = AttackAction.FIREBALL;
            return;
        }
        // 5. 飞刀：3~20 格且手持飞刀且有视线
        if (this.mob.isHolding(ModItems.THROWABLE_DAGGER)
                && distSq > DAGGER_MIN_SQ && distSq <= DAGGER_MAX_SQ && sight) {
            this.current = AttackAction.DAGGER;
            return;
        }
        // 6. 近战：<=3.5 格且高度差在近战允许范围内
        if (distSq <= MELEE_RANGE_SQ && vertDiff <= MELEE_VERTICAL_DIFF) {
            this.current = AttackAction.MELEE;
            return;
        }
        // 兜底：高度差允许时仍以近战追击，否则不攻击
        this.current = vertDiff <= MELEE_VERTICAL_DIFF ? AttackAction.MELEE : AttackAction.NONE;
    }

    public boolean isMelee() {
        return this.current == AttackAction.MELEE;
    }

    public boolean isDagger() {
        return this.current == AttackAction.DAGGER;
    }

    public boolean isFireball() {
        return this.current == AttackAction.FIREBALL;
    }

    public boolean isFireballJump() {
        return this.current == AttackAction.FIREBALL_JUMP;
    }

    public boolean isEnderPearl() {
        return this.current == AttackAction.ENDER_PEARL;
    }

    public boolean isStuckMining() {
        return this.current == AttackAction.STUCK_MINING;
    }
}