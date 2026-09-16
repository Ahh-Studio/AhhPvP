package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.entities.FireballEntity;
import com.aiden.pvp.items.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FireballJumpGoal extends Goal {
    private final MurdererEntity mob;
    private int jumpTicks;
    private boolean jumpScheduled;
    private boolean fireballFired;

    public FireballJumpGoal(MurdererEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.level().isClientSide()) return false;
        // 由 CombatManager 统一决策：仅当当前动作是烈焰弹跳跃才激活
        return this.mob.combatManager.isFireballJump();
    }

    @Override
    public void start() {
        super.start();
        this.jumpTicks = 0;
        this.jumpScheduled = false;
        this.fireballFired = false;
    }

    @Override
    public void tick() {
        this.jumpTicks++;
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        // 疾跑冲向目标，保持前向动量
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.mob.setSprinting(true);
        this.mob.getNavigation().moveTo(target, 1.3);

        if (!this.jumpScheduled) {
            if (this.jumpTicks >= 4) { // 先疾跑几tick再起跳
                this.mob.jumpFromGround();
                this.jumpScheduled = true;
            }
        } else if (!this.fireballFired && this.jumpTicks >= 8 && this.mob.level() instanceof ServerLevel serverWorld) {
            // 发射瞬间若目标已贴近到近战距离（<=3.5格），放弃弹射，避免把自己崩歪/贴脸过头
            if (target.distanceToSqr(this.mob) <= MurdererCombatManager.MELEE_RANGE_SQ) return;

            // 朝目标方向斜向下发射，使爆炸把杀手推往目标而非垂直崩离
            Vec3 dir = target.position().add(0, this.mob.getEyeHeight() - 0.5, 0)
                    .subtract(this.mob.getEyePosition()).normalize();

            // 水平方向上朝目标的单位向量
            Vec3 towardTarget = new Vec3(dir.x, 0, dir.z);

            // 施加朝目标的明确前向+上冲量，保证引爆后往前崩并明显上冲，不依赖爆炸几何
            Vec3 currentMotion = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(
                    currentMotion.x + towardTarget.x * 0.7,
                    currentMotion.y + 0.7,
                    currentMotion.z + towardTarget.z * 0.7
            );

            FireballEntity fireballEntity = new FireballEntity(this.mob, this.mob.level(), ModItems.FIREBALL.getDefaultInstance());
            fireballEntity.setPosRaw(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            this.mob.holdFireballInHand();
            // 垂直向下引爆，爆炸点落在脚底附近，主要提供垂直升空，避免火球漂到身前导致向后崩
            fireballEntity.shoot(0.0, -1.0, 0.0, 1.2F, 0.0F);

            serverWorld.addFreshEntity(fireballEntity);

            this.fireballFired = true;
            // 设置独立跳跃冷却（不受定向烈焰弹冷却约束），保证能高频进近
            this.mob.fireballJumpCooldownTicks = this.mob.getRandom().nextInt(
                    MurdererEntity.FIREBALL_JUMP_MIN_COOLDOWN, MurdererEntity.FIREBALL_JUMP_MAX_COOLDOWN + 1);
            this.mob.fireballCooldownTicks = this.mob.getRandom().nextInt(MurdererEntity.FIREBALL_MIN_COOLDOWN, MurdererEntity.FIREBALL_MAX_COOLDOWN + 1);
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && this.jumpTicks < 10;
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setSprinting(false);
    }
}