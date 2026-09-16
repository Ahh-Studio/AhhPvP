package com.aiden.pvp.entities.murderer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class MeleeAttackGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private long lastUpdateTime;

    public MeleeAttackGoal(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /** 目标在垂直方向上超过此高度差时，认为近战无法到达，让位给远程 Goal */
    private static final double MAX_MELEE_VERTICAL_DIFF = 2.5;

    @Override
    public boolean canUse() {
        long l = this.mob.level().getGameTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            }
            if (!livingEntity.isAlive()) {
                return false;
            }
            // 由 CombatManager 统一决策：仅当当前动作是近战才激活，避免同优先级竞争
            if (this.mob instanceof MurdererEntity murderer && !murderer.combatManager.isMelee()) {
                return false;
            }
            // 目标垂直距离过大（搭高台、骑骆驼等），近战永远够不到，让位给远程 Goal
            if (Math.abs(this.mob.getY() - livingEntity.getY()) > MAX_MELEE_VERTICAL_DIFF) {
                return false;
            }
            // 吃金苹果期间禁止攻击
            if (this.mob instanceof MurdererEntity murderer && murderer.isEatingGoldenApple()) {
                return false;
            }
            this.path = this.mob.getNavigation().createPath(livingEntity, 0);
            return (this.path != null || this.mob.isWithinMeleeAttackRange(livingEntity)) && this.mob.distanceToSqr(livingEntity) <= 100;
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isDone();
        } else {
            return this.mob.isWithinHome(livingEntity.blockPosition()) && !(livingEntity instanceof Player playerEntity && (playerEntity.isSpectator() || playerEntity.isCreative()));
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speed);
        this.mob.setAggressive(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null && !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget(null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
            if ((this.pauseWhenMobIdle || this.mob.getSensing().hasLineOfSight(livingEntity))
                    && this.updateCountdownTicks <= 0
                    && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
                            || livingEntity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0
                            || this.mob.getRandom().nextFloat() < 0.05F
            )) {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();
                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                double d = this.mob.distanceToSqr(livingEntity);
                if (d > 1024.0) {
                    this.updateCountdownTicks += 10;
                } else if (d > 256.0) {
                    this.updateCountdownTicks += 5;
                }

                if (!this.mob.getNavigation().moveTo(livingEntity, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.adjustedTickDelay(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.attack(livingEntity);
        }
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            // 仅在真正命中时才触发 W-Tap 冻结，挥空不冻结
            boolean hit = this.mob.doHurtTarget((ServerLevel) this.mob.level(), target);
            if (hit && this.mob instanceof MurdererEntity murderer) {
                murderer.triggerWTapPause();
            }
        }
    }

    protected void resetCooldown() {
        double attackSpeed = this.mob.getAttributeValue(Attributes.ATTACK_SPEED);
        int dynamicCooldown = (int) (20 / attackSpeed);
        this.cooldown = this.adjustedTickDelay(Math.max(1, dynamicCooldown));
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isWithinMeleeAttackRange(target) && this.mob.getSensing().hasLineOfSight(target);
    }
}