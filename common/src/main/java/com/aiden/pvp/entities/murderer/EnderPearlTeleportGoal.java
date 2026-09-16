package com.aiden.pvp.entities.murderer;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EnderPearlTeleportGoal extends Goal {
    private final MurdererEntity mob;

    public EnderPearlTeleportGoal(MurdererEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.level().isClientSide()) return false;
        // 由 CombatManager 统一决策：仅当当前动作是末影珍珠才激活
        return this.mob.combatManager.isEnderPearl();
    }

    @Override
    public void start() {
        super.start();
        if (this.mob.getTarget() == null) return;

        Level world = this.mob.level();
        if (world.isClientSide()) return;

        ThrownEnderpearl enderPearl = new ThrownEnderpearl(EntityType.ENDER_PEARL, world);
        enderPearl.setOwner(this.mob);
        enderPearl.setPosRaw(mob.getX(), mob.getEyeY(), mob.getZ());

        Vec3 targetPos = this.mob.getTarget().getEyePosition();
        Vec3 mobPos = this.mob.getEyePosition();
        Vec3 direction = targetPos.subtract(mobPos).normalize();

        double i = direction.y * 1.2;

        if (i >= 0) {
            enderPearl.shoot(
                    1.2 * direction.x, 2.0 * i + 0.5, 1.2 * direction.z,
                    1.2F, 1.0F
            );
        } else if (i > -0.4 && i < -0.1) {
            enderPearl.shoot(
                    1.2 * direction.x, 1 / i * -0.05, 1.2 * direction.z,
                    1.2F, 1.0F
            );
        } else {
            enderPearl.shoot(
                    1.2 * direction.x, 0.25 * i, 1.2 * direction.z,
                    1.2F, 1.0F
            );
        }

        this.mob.playSound(SoundEvents.ENDER_PEARL_THROW, 1.0F, 1.0F);
        this.mob.lookAt(this.mob.getTarget(), 30.0F, 30.0F);

        world.addFreshEntity(enderPearl);

        this.mob.enderPearlCooldownTicks = mob.getRandom().nextInt(MurdererEntity.ENDER_PEARL_MIN_COOLDOWN, MurdererEntity.ENDER_PEARL_MAX_COOLDOWN + 1);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}