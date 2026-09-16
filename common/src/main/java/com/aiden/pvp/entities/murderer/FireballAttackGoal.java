package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.entities.FireballEntity;
import com.aiden.pvp.items.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FireballAttackGoal extends Goal {
    private final MurdererEntity mob;

    public FireballAttackGoal(MurdererEntity mob) {
        this.mob = mob;
        // 加 MOVE：远程攻击同时向目标推进，防止被甩
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.level().isClientSide()) return false;
        // 由 CombatManager 统一决策：仅当当前动作是烈焰弹才激活
        return this.mob.combatManager.isFireball();
    }

    @Override
    public void start() {
        super.start();
        if (this.mob.getTarget() == null) return;

        // 远程攻击的同时向目标推进，防止被甩开（与飞刀追击速度一致）
        this.mob.getNavigation().moveTo(this.mob.getTarget(), MurdererCombatManager.CHASE_SPEED);

        if (this.mob.level() instanceof ServerLevel serverWorld) {
            FireballEntity fireballEntity = new FireballEntity(this.mob, this.mob.level(), ModItems.FIREBALL.getDefaultInstance());
            fireballEntity.setPosRaw(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            this.mob.holdFireballInHand();

            Vec3 targetPos = this.mob.getTarget().position().subtract(0, 0.5, 0);
            Vec3 mobPos = this.mob.getEyePosition();
            Vec3 direction = targetPos.subtract(mobPos).normalize();

            this.mob.lookAt(this.mob.getTarget(), 30.0F, 30.0F);

            fireballEntity.shoot(
                    1.2 * direction.x,
                    1.2 * direction.y,
                    1.2 * direction.z,
                    1.2F,
                    1.0F
            );

            serverWorld.addFreshEntity(fireballEntity);
        }

        // 设置冷却，避免无限连射
        this.mob.fireballCooldownTicks = this.mob.getRandom().nextInt(
                MurdererEntity.FIREBALL_MIN_COOLDOWN,
                MurdererEntity.FIREBALL_MAX_COOLDOWN + 1
        );
        // 仅 combo 触发时清零连击计数（连击 >2 触发，原 >3）
        if (this.mob.comboHitsTaken > MurdererCombatManager.COMBO_FIREBALL_TRIGGER) {
            this.mob.comboHitsTaken = 0;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}