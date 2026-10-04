package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.entities.FireballEntity;
import com.aiden.pvp.items.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 泛作战"战斗中补血"模式：
 * <ul>
 *  <li>血量低于一半（<50%）且库存金苹果>0 时进入；战斗补到 80%（脱战补到 100%）后退出。</li>
 *  <li>每吃 1 个金苹果消耗 1 个库存（初始 16），吃光则该次生命永不复血。</li>
 *  <li>无冷却：吃一个后等待再生 II 效果结束（约 5s）再吃下一个，避免浪费效果。</li>
 *  <li>补血期间完全停止攻击；被近身（<20 格）或任意扣血→撤退（可用末影珍珠瞬移）到
 *      &gt;25 格才恢复进食；撤退持续超时仍拉不开→朝目标头部及其四周连射烈焰弹逼退。</li>
 * </ul>
 */
public class GoldenAppleHealGoal extends Goal {
    /** 吃金苹果的视觉/锁定期（tick） */
    private static final int EAT_DURATION_TICKS = 32;
    /** 吃下一个前需等待的再生 II 持续时长（5s），避免效果叠加浪费 */
    private static final int REGEN_DURATION_TICKS = 5 * 20;
    /** 被近身/扣血触发撤退的距离平方（20格） */
    private static final double RETREAT_THRESHOLD_SQ = 400.0;
    /** 撤退到安全、可恢复进食的距离平方（25 格） */
    private static final double RETREAT_TARGET_SQ = 625.0;
    /** 战斗中补血的目标血量比例 */
    private static final double COMBAT_FULL_RATIO = 0.8;
    /** 撤退持续此 tick 数仍不安全则触发烈焰弹逼退（2.5s） */
    private static final int BARRAGE_WAIT_TICKS = 50;
    /** 烈焰弹逼退连射数量（1 发精确瞄准头部 + 其余绕头散布） */
    private static final int BARRAGE_COUNT = 4;
    /** 连射间隔（tick） */
    private static final int BARRAGE_SHOT_INTERVAL = 5;
    /** 撤退时尝试末影珍珠瞬移的距离上限平方（30 格内更有效） */
    private static final double PEARL_ATTEMPT_SQ = 900.0;

    private final MurdererEntity mob;

    /** 进入模式时判定：脱战进入则补到 100%，战斗中进入则补到 80%（目标中途消失也不变） */
    private boolean fullHeal;

    private int eatLockTicks;
    private int regenWaitTicks;
    private boolean retreating;
    private int retreatTicks;
    private boolean pearlThrown;
    private int barrageRemaining;
    private int barrageShotCooldown;

    public GoldenAppleHealGoal(MurdererEntity mob) {
        this.mob = mob;
        // 占用 MOVE/JUMP/LOOK，压过所有攻击 Goal（它们都需要 MOVE/LOOK），保证补血期间不攻击
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.level().isClientSide()) return false;
        if (this.mob.goldenApples <= 0) return false; // 吃光后永不复血
        if (this.mob.isEatingGoldenApple()) return false;
        return this.mob.getHealth() / this.mob.getMaxHealth() < 0.5;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.goldenApples <= 0) return false;
        double fullRatio = this.fullHeal ? 1.0 : COMBAT_FULL_RATIO;
        return this.mob.getHealth() / this.mob.getMaxHealth() < fullRatio;
    }

    @Override
    public void start() {
        // 按进入时的作战状态锁定补血目标：脱战→100%，战斗→80%
        this.fullHeal = !this.hasCombatTarget();
        this.eatLockTicks = 0;
        this.regenWaitTicks = 0;
        this.retreating = false;
        this.retreatTicks = 0;
        this.pearlThrown = false;
        this.barrageRemaining = 0;
        this.barrageShotCooldown = 0;
        this.mob.inHealMode = true;
    }

    @Override
    public void stop() {
        this.mob.inHealMode = false;
        this.mob.eatingGoldenAppleTicks = 0;
        this.mob.stopUsingItem();
        this.mob.getNavigation().stop();
        this.mob.setSprinting(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        boolean combat = target != null && target.isAlive();
        double distSq = combat ? target.distanceToSqr(this.mob) : Double.POSITIVE_INFINITY;
        // 受伤（hurtServer 在同 tick 置标志）或战斗中被近身（<20 格）
        boolean threatened = this.mob.inHealModeTookDamage() || (combat && distSq < RETREAT_THRESHOLD_SQ);

        if (combat) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        // 达到退出目标直接结束（由 canContinueToUse 兜底，这里也主动收尾）
        double fullRatio = this.fullHeal ? 1.0 : COMBAT_FULL_RATIO;
        if (this.mob.getHealth() / this.mob.getMaxHealth() >= fullRatio) {
            return;
        }

        // 一旦开始撤退就持续到安全距离（>25 格）为止
        if (combat && (this.retreating || threatened)) {
            this.retreating = true;
            this.handleRetreat(target);
            this.tickBarrage();
            if (distSq >= RETREAT_TARGET_SQ) { // 已安全，恢复正常进食
                this.retreating = false;
                this.retreatTicks = 0;
                this.pearlThrown = false;
                this.barrageRemaining = 0;
            }
        }

        // 目标消失/脱战时不再撤退，恢复进食（如进入时为脱战模式，则补到 100%）
        if (!combat) {
            this.retreating = false;
        }

        // 非撤退且未在吃 → 尝试进食
        if (!this.retreating) {
            this.tryEat();
        }

        // 收尾冷却计时
        if (this.eatLockTicks > 0) this.eatLockTicks--;
        if (this.regenWaitTicks > 0) this.regenWaitTicks--;
        this.mob.setSprinting(false);
    }

    /** 撤退：跑离目标；末影珍珠可用且拉不开时先瞬移一次。 */
    private void handleRetreat(LivingEntity target) {
        this.retreatTicks++;
        this.mob.setSprinting(true);
        this.mob.getNavigation().stop();

        if (this.mob.enderPearlCooldownTicks <= 0 && !this.mob.isUnderWater()
                && !this.pearlThrown && target.distanceToSqr(this.mob) < PEARL_ATTEMPT_SQ) {
            this.throwEnderpearlAway(target);
        }

        // 向远离目标方向移动
        Vec3 away = this.mob.position().subtract(target.position());
        Vec3 dir = away.x == 0 && away.z == 0 ? new Vec3(0, 0, 1) : new Vec3(away.x, 0, away.z).normalize();
        double dist = 14.0;
        this.mob.getNavigation().moveTo(
                this.mob.getX() + dir.x * dist, this.mob.getY(), this.mob.getZ() + dir.z * dist, 1.0);
    }

    /** 向远离目标方向抛末影珍珠瞬移脱身。 */
    private void throwEnderpearlAway(LivingEntity target) {
        ThrownEnderpearl pearl = new ThrownEnderpearl(EntityTypes.ENDER_PEARL, this.mob.level());
        pearl.setOwner(this.mob);
        pearl.setPosRaw(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        Vec3 away = this.mob.position().subtract(target.position());
        Vec3 dir = new Vec3(away.x, 0, away.z).normalize();
        pearl.shoot(1.2 * dir.x, 0.5, 1.2 * dir.z, 1.2F, 1.0F);
        this.mob.playSound(SoundEvents.ENDER_PEARL_THROW, 1.0F, 1.0F);
        this.mob.level().addFreshEntity(pearl);
        this.mob.enderPearlCooldownTicks = this.mob.getRandom().nextInt(
                MurdererEntity.ENDER_PEARL_MIN_COOLDOWN, MurdererEntity.ENDER_PEARL_MAX_COOLDOWN + 1);
        this.pearlThrown = true;
    }

    /** 撤退持续超时拉不开→朝目标头部及四周连射烈焰弹逼退。 */
    private void tickBarrage() {
        if (this.retreatTicks > BARRAGE_WAIT_TICKS && this.barrageRemaining == 0) {
            this.barrageRemaining = BARRAGE_COUNT;
        }
        LivingEntity target = this.mob.getTarget();
        if (target == null || this.barrageRemaining <= 0) return;
        if (this.barrageShotCooldown > 0) {
            this.barrageShotCooldown--;
            return;
        }
        // 每次射 1 发：1 发精确瞄准头部，其余绕头随机偏移覆盖走位
        int which = BARRAGE_COUNT - this.barrageRemaining;
        Vec3 head = target.getEyePosition();
        Vec3 aim = head;
        if (which > 0) {
            float ang = this.mob.getRandom().nextFloat() * (float) (Math.PI * 2);
            float r = 0.8F + this.mob.getRandom().nextFloat() * 1.4F;
            aim = head.add(Math.cos(ang) * r, (this.mob.getRandom().nextFloat() - 0.5F) * 0.8, Math.sin(ang) * r);
        }
        this.fireFireballAt(aim);
        this.barrageRemaining--;
        if (this.barrageRemaining > 0) this.barrageShotCooldown = BARRAGE_SHOT_INTERVAL;
    }

    private void fireFireballAt(Vec3 aim) {
        if (!(this.mob.level() instanceof ServerLevel serverWorld)) return;
        FireballEntity fireballEntity = new FireballEntity(this.mob, this.mob.level(), ModItems.FIREBALL.getDefaultInstance());
        fireballEntity.setPosRaw(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        this.mob.holdFireballInHand();
        Vec3 dir = aim.subtract(this.mob.getEyePosition()).normalize();
        fireballEntity.shoot(1.2 * dir.x, 1.2 * dir.y, 1.2 * dir.z, 1.2F, 1.0F);
        serverWorld.addFreshEntity(fireballEntity);
    }

    /** 安全且满足条件时吃一个金苹果（等上一次再生结束后再吃）。 */
    private void tryEat() {
        if (this.eatLockTicks > 0 || this.regenWaitTicks > 0) return;

        this.mob.goldenApples--;
        if (this.mob.goldenApples <= 0) return; // 吃光，本次生命不再补血

        this.eatLockTicks = EAT_DURATION_TICKS;
        this.regenWaitTicks = REGEN_DURATION_TICKS;
        this.mob.eatingGoldenAppleTicks = EAT_DURATION_TICKS;
        this.mob.stopUsingItem();

        // 原版金苹果效果：再生 II 5s + 吸收 I 120s
        this.mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_DURATION_TICKS, 1));
        this.mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2 * 60 * 20, 0));
    }

    private boolean hasCombatTarget() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }
}