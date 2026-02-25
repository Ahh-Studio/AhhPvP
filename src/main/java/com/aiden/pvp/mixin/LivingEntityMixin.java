package com.aiden.pvp.mixin;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.mixin.accessor.LivingEntityAccessor;
import com.aiden.pvp.mixin.invoker.LivingEntityInvoker;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean canBreatheUnderwater();

    @Inject(
            method = "knockback",
            at = @At("HEAD"),
            cancellable = true,
            order = 999
    )
    public void takeKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity instance = (LivingEntity) (Object) this;

        strength *= 1.0 - instance.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (strength > 0.0) {
            instance.needsSync = true;
            Vec3 vec3 = instance.getDeltaMovement();

            while (x * x + z * z < 1.0E-5F) {
                x = (Math.random() - Math.random()) * 0.01;
                z = (Math.random() - Math.random()) * 0.01;
            }

            Vec3 vec32 = new Vec3(x, 0.0, z).normalize().scale(strength);
            instance.setDeltaMovement(
                    vec3.x / 2.0 - vec32.x,
                    instance.onGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : Math.min(0.2, vec3.y / 4.0 + strength / 2),
                    vec3.z / 2.0 - vec32.z);
        }

        ci.cancel();
    }

    @Inject(
            method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true,
            order = 999
    )
    public void damage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity instance = (LivingEntity) (Object) this;
        LivingEntityAccessor accessor = (LivingEntityAccessor) instance;
        LivingEntityInvoker invoker = (LivingEntityInvoker) instance;

        if (instance.isInvulnerableTo(level, source)) {
            cir.setReturnValue(false); return;
        } else if (instance.isDeadOrDying()) {
            cir.setReturnValue(false); return;
        } else if (source.is(DamageTypeTags.IS_FIRE) && instance.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            cir.setReturnValue(false); return;
        } else {
            accessor.getDamageContainers().push(new net.neoforged.neoforge.common.damagesource.DamageContainer(source, amount));
            if (net.neoforged.neoforge.common.CommonHooks.onEntityIncomingDamage(instance, accessor.getDamageContainers().peek())) {
                cir.setReturnValue(false); return;
            }
            if (instance.isSleeping()) {
                instance.stopSleeping();
            }

            instance.setNoActionTime(0);
            amount = accessor.getDamageContainers().peek().getNewDamage(); //Neo: enforce damage container as source of truth for damage amount
            if (amount < 0.0F) {
                amount = 0.0F;
            }

            ItemStack itemstack = instance.getUseItem();
            float f = instance.applyItemBlocking(level, source, amount);
            amount -= f;
            boolean flag = f > 0.0F;
            if (source.is(DamageTypeTags.IS_FREEZING) && instance.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                amount *= 5.0F;
            }

            if (source.is(DamageTypeTags.DAMAGES_HELMET) && !instance.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                invoker.invokedHurtHelmet(source, amount);
                amount *= 0.75F;
            }

            if (Float.isNaN(amount) || Float.isInfinite(amount)) {
                amount = Float.MAX_VALUE;
            }
            accessor.getDamageContainers().peek().setNewDamage(amount); //update container with vanilla changes

            boolean flag1 = true;
            if (instance.invulnerableTime > 10.0F && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                if (amount <= accessor.getLastHurt()) {
                    accessor.getDamageContainers().pop();
                    cir.setReturnValue(false);
                    return;
                }

                accessor.getDamageContainers().peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.INVULNERABILITY, accessor.getLastHurt());
                invoker.invokedApplyDamage(level, source, amount - accessor.getLastHurt());
                accessor.setLastDamageTaken(amount);
                flag1 = false;
            } else {
                int phdi = 10;
                if (instance.level() instanceof ServerLevel serverWorld) {
                    phdi = serverWorld.getGameRules().get(ModGameRules.PHDI.get());
                }
                accessor.setLastDamageTaken(amount);
                instance.invulnerableTime = accessor.getDamageContainers().peek().getPostAttackInvulnerabilityTicks();
                instance.invulnerableTime = 2 * phdi;
                invoker.invokedApplyDamage(level, source, amount);
                instance.hurtDuration = phdi;
                instance.hurtTime = instance.hurtDuration;
            }

            amount = accessor.getDamageContainers().peek().getNewDamage(); //update local with container value
            invoker.invokedBecomeAngry(source);
            invoker.invokedSetAttackingPlayer(source);
            if (flag1) {
                BlocksAttacks blocksattacks = itemstack.get(DataComponents.BLOCKS_ATTACKS);
                if (flag && blocksattacks != null) {
                    blocksattacks.onBlocked(level, instance);
                } else {
                    level.broadcastDamageEvent(instance, source);
                }

                if (!source.is(DamageTypeTags.NO_IMPACT) && (!flag || amount > 0.0F)) {
                    instance.hurtMarked = true;
                }

                if (!source.is(DamageTypeTags.NO_KNOCKBACK)) {
                    double d0 = 0.0;
                    double d1 = 0.0;
                    if (source.getDirectEntity() instanceof Projectile projectile) {
                        DoubleDoubleImmutablePair doubledoubleimmutablepair = projectile.calculateHorizontalHurtKnockbackDirection(instance, source);
                        d0 = -doubledoubleimmutablepair.leftDouble();
                        d1 = -doubledoubleimmutablepair.rightDouble();
                    } else if (source.getSourcePosition() != null) {
                        d0 = source.getSourcePosition().x() - instance.getX();
                        d1 = source.getSourcePosition().z() - instance.getZ();
                    }

                    instance.knockback(0.4F, d0, d1);
                    if (!flag) {
                        instance.indicateDamage(d0, d1);
                    }
                }
            }

            if (instance.isDeadOrDying()) {
                if (!invoker.invokedTryUseDeathProtector(source)) {
                    if (flag1) {
                        instance.makeSound(invoker.invokedGetDeathSound());
                        invoker.invokedPlayThornsSound(source);
                    }

                    instance.die(source);
                }
            } else if (flag1) {
                invoker.invokedPlayHurtSound(source);
                invoker.invokedPlayThornsSound(source);
            }

            boolean flag2 = !flag || amount > 0.0F;
            if (flag2) {
                accessor.setLastDamageSource(source);
                accessor.setLastDamageTaken(instance.level().getGameTime());

                for (MobEffectInstance mobeffectinstance : instance.getActiveEffects()) {
                    mobeffectinstance.onMobHurt(level, instance, source, amount);
                }
            }

            if (instance instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, source, amount, amount, flag);
                if (f > 0.0F && f < 3.4028235E37F) {
                    serverPlayer.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f * 10.0F));
                }
            }

            if (source.getEntity() instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayer, instance, source, amount, amount, flag);
            }

            accessor.getDamageContainers().pop();
            cir.setReturnValue(flag2);
        }
    }
}
