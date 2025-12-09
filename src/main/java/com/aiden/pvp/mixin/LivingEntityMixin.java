package com.aiden.pvp.mixin;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.mixin.accessor.LivingEntityAccessor;
import com.aiden.pvp.mixin.invoker.LivingEntityInvoker;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "takeKnockback",
            at = @At("HEAD"),
            cancellable = true,
            order = 999
    )
    public void takeKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity instance = (LivingEntity) (Object) this;

        strength *= 1.0 - instance.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
        if (!(strength <= 0.0)) {
            instance.velocityDirty = true;
            Vec3d vec3d = instance.getVelocity();

            while (x * x + z * z < 1.0E-5F) {
                x = (Math.random() - Math.random()) * 0.01;
                z = (Math.random() - Math.random()) * 0.01;
            }

            Vec3d vec3d2 = new Vec3d(x, 0.0, z).normalize().multiply(strength);
            instance.setVelocity(
                    vec3d.x / 2.0 - vec3d2.x,
                    instance.isOnGround() ? Math.min(0.4, vec3d.y / 2.0 + strength) : Math.min(0.2, vec3d.y / 4.0 + strength / 2),
                    vec3d.z / 2.0 - vec3d2.z);
        }
        ci.cancel();
    }


    /**
     * @author Aiden
     * @reason Changed PHDI
     */
    @Inject(
            method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true,
            order = 999
    )
    public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity instance = (LivingEntity) (Object) this;
        LivingEntityAccessor accessor = (LivingEntityAccessor) instance;
        LivingEntityInvoker invoker = (LivingEntityInvoker) instance;

        if (instance.isInvulnerableTo(world, source)) {
            cir.setReturnValue(false);
            return;
        } else if (instance.isDead()) {
            cir.setReturnValue(false);
            return;
        } else if (source.isIn(DamageTypeTags.IS_FIRE) && instance.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            cir.setReturnValue(false);
            return;
        } else {
            if (instance.isSleeping()) {
                instance.wakeUp();
            }

            instance.setDespawnCounter(0);
            if (amount < 0.0F) {
                amount = 0.0F;
            }

            ItemStack itemStack = instance.getActiveItem();
            float g = instance.getDamageBlockedAmount(world, source, amount);
            amount -= g;
            boolean bl = g > 0.0F;
            if (source.isIn(DamageTypeTags.IS_FREEZING) && instance.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                amount *= 5.0F;
            }

            if (source.isIn(DamageTypeTags.DAMAGES_HELMET) && !instance.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                instance.damageHelmet(source, amount);
                amount *= 0.75F;
            }

            if (Float.isNaN(amount) || Float.isInfinite(amount)) {
                amount = Float.MAX_VALUE;
            }

            boolean bl2 = true;
            if (instance.timeUntilRegen > 10.0F && !source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) {
                if (amount <= accessor.getLastDamageTaken()) {
                    cir.setReturnValue(false);
                    return;
                }

                invoker.invokedApplyDamage(world, source, amount - accessor.getLastDamageTaken());
                accessor.setLastDamageTaken(amount);
                bl2 = false;
            } else {
                int phdi = 10;
                if (instance.getEntityWorld() instanceof ServerWorld serverWorld) {
                    phdi = serverWorld.getGameRules().getInt(ModGameRules.PHDI);
                }
                accessor.setLastDamageTaken(amount);
                instance.timeUntilRegen = 2 * phdi;
                invoker.invokedApplyDamage(world, source, amount);
                instance.maxHurtTime = phdi;
                instance.hurtTime = instance.maxHurtTime;
            }

            invoker.invokedBecomeAngry(source);
            invoker.invokedSetAttackingPlayer(source);
            if (bl2) {
                BlocksAttacksComponent blocksAttacksComponent = itemStack.get(DataComponentTypes.BLOCKS_ATTACKS);
                if (bl && blocksAttacksComponent != null) {
                    blocksAttacksComponent.playBlockSound(world, instance);
                } else {
                    world.sendEntityDamage(instance, source);
                }

                if (!source.isIn(DamageTypeTags.NO_IMPACT) && (!bl || amount > 0.0F)) {
                    instance.velocityModified = true;
                }

                if (!source.isIn(DamageTypeTags.NO_KNOCKBACK)) {
                    double d = 0.0;
                    double e = 0.0;
                    if (source.getSource() instanceof ProjectileEntity projectileEntity) {
                        DoubleDoubleImmutablePair doubleDoubleImmutablePair = projectileEntity.getKnockback(instance, source);
                        d = -doubleDoubleImmutablePair.leftDouble();
                        e = -doubleDoubleImmutablePair.rightDouble();
                    } else if (source.getPosition() != null) {
                        d = source.getPosition().getX() - instance.getX();
                        e = source.getPosition().getZ() - instance.getZ();
                    }

                    instance.takeKnockback(0.4F, d, e);
                    if (!bl) {
                        instance.tiltScreen(d, e);
                    }
                }
            }

            if (instance.isDead()) {
                if (!invoker.invokedTryUseDeathProtector(source)) {
                    if (bl2) {
                        instance.playSound(invoker.invokedGetDeathSound());
                        invoker.invokedPlayThornsSound(source);
                    }

                    instance.onDeath(source);
                }
            } else if (bl2) {
                invoker.invokedPlayHurtSound(source);
                invoker.invokedPlayThornsSound(source);
            }

            boolean bl3 = !bl || amount > 0.0F;
            if (bl3) {
                accessor.setLastDamageSource(source);
                accessor.setLastDamageTime(instance.getEntityWorld().getTime());

                for (StatusEffectInstance statusEffectInstance : instance.getStatusEffects()) {
                    statusEffectInstance.onEntityDamage(world, instance, source, amount);
                }
            }

            if (instance instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.ENTITY_HURT_PLAYER.trigger(serverPlayerEntity, source, amount, amount, bl);
                if (g > 0.0F && g < 3.4028235E37F) {
                    serverPlayerEntity.increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
                }
            }

            if (source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, instance, source, amount, amount, bl);
            }

            cir.setReturnValue(bl3);
            return;
        }
    }
}
