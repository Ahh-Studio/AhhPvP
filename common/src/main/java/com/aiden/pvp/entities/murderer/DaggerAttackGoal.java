package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.entities.DaggerEntity;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.items.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class DaggerAttackGoal extends Goal {
    private final MurdererEntity actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private int combatTicks = -1;

    public DaggerAttackGoal(MurdererEntity actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canUse() {
        // 由 CombatManager 统一决策：仅当当前动作是飞刀才激活
        if (!this.actor.combatManager.isDagger()) return false;
        if (!this.isHoldingDagger()) return false;
        return true;
    }

    protected boolean isHoldingDagger() {
        return this.actor.isHolding(ModItems.THROWABLE_DAGGER);
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.actor.getNavigation().isDone()) && this.isHoldingDagger();
    }

    @Override
    public void start() {
        super.start();
        this.actor.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAggressive(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        this.actor.stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            double d = this.actor.distanceToSqr(livingEntity);
            boolean bl = this.actor.getSensing().hasLineOfSight(livingEntity);
            boolean bl2 = this.targetSeeingTicker > 0;
            this.actor.lookAt(livingEntity, 30.0F, 30.0F);

            if (bl != bl2) {
                this.targetSeeingTicker = 0;
            }

            if (bl) {
                this.targetSeeingTicker++;
            } else {
                this.targetSeeingTicker--;
            }

            if (!(d > this.squaredRange) && this.targetSeeingTicker >= 20) {
                // 飞刀攻击期间始终向目标进近，速度与远距离追击（CHASE_SPEED）完全一致
                this.actor.getNavigation().moveTo(livingEntity, this.speed);
                this.combatTicks++;
            } else {
                this.combatTicks = -1;
            }

            if (this.combatTicks >= 20) {
                if (this.actor.getRandom().nextFloat() < 0.3) {
                    this.movingToLeft = !this.movingToLeft;
                }

                this.combatTicks = 0;
            }

            if (this.combatTicks > -1) {
                // 始终向前（朝向目标）全速进近，速度与远距离追击一致；仅用侧移走位闪避
                this.actor.getMoveControl().strafe(1.0F, this.movingToLeft ? 0.5F : -0.5F);
                if (this.actor.getControlledVehicle() instanceof Mob mobEntity) {
                    mobEntity.lookAt(livingEntity, 30.0F, 30.0F);
                }

                this.actor.lookAt(livingEntity, 30.0F, 30.0F);
            } else {
                this.actor.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            }

            if (this.actor.isUsingItem()) {
                if (!bl && this.targetSeeingTicker < -60) {
                    this.actor.stopUsingItem();
                } else if (bl) {
                    int i = this.actor.getTicksUsingItem();
                    if (i >= 20) {
                        this.actor.stopUsingItem();

                        DaggerEntity daggerEntity = getDaggerEntity();
                        this.actor.level().addFreshEntity(daggerEntity);

                        this.actor.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.actor.getRandom().nextFloat() * 0.4F + 0.8F));

                        this.cooldown = this.attackInterval;
                    }
                }
            } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                this.actor.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.actor, ModItems.THROWABLE_DAGGER));
            }
        }
    }

    private @NotNull DaggerEntity getDaggerEntity() {
        float f = -Mth.sin(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180));
        float g = -Mth.sin((this.actor.getXRot() + 0.0F) * ((float)Math.PI / 180));
        float h = Mth.cos(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180));
        return throwDaggerAt(this.actor, new Vec3(f, g, h));
    }

    /** 从指定实体位置朝给定方向投掷一把飞刀（仅负责创建与瞄准，不加入世界、不播放音效） */
    static DaggerEntity throwDaggerAt(LivingEntity thrower, Vec3 direction) {
        DaggerEntity daggerEntity = new DaggerEntity(ModEntityTypes.DAGGER, thrower.level());
        daggerEntity.setOwner(thrower);
        daggerEntity.setPosRaw(thrower.getX(), thrower.getEyeY(), thrower.getZ());
        daggerEntity.shoot(direction.x, direction.y, direction.z, 1.2F, 0.0F);
        return daggerEntity;
    }
}