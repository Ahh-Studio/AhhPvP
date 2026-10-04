package com.aiden.pvp.mixin;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.entities.murderer.MurdererEntity;
import com.aiden.pvp.items.SwordItem;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private boolean blockedByItemActive = false;

    @Inject(method = "blockedByItem", at = @At("HEAD"))
    private void onBlockedByItem(LivingEntity defender, DamageSource source, float damage, CallbackInfo ci) {
        this.blockedByItemActive = true;
    }

    @Inject(method = "blockedByItem", at = @At("RETURN"))
    private void onBlockedByItemReturn(LivingEntity defender, DamageSource source, float damage, CallbackInfo ci) {
        this.blockedByItemActive = false;
    }

    @Inject(
            method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void takeKnockback(double power, double xd, double zd, final DamageSource source, final float damage, final boolean comesFromEffect, CallbackInfo ci) {
        LivingEntity instance = (LivingEntity) (Object) this;

        if (this.blockedByItemActive) {
            ci.cancel();
            return;
        }

        boolean isSwordBlocking = false;
        ItemStack blockingItem = instance.getItemBlockingWith();
        ItemStack useItem = instance.getUseItem();
        
        if (blockingItem != null && blockingItem.getItem() instanceof SwordItem) {
            isSwordBlocking = true;
        } else if (instance.isBlocking() && useItem != null && !useItem.isEmpty() && useItem.getItem() instanceof SwordItem) {
            isSwordBlocking = true;
        }
        
        if (isSwordBlocking) {
            power = 0.4F;
        }

        // 杀手 Jump Reset：受击瞬间起跳时，减免大部分击退
        if (instance instanceof MurdererEntity murderer && murderer.jumpResetQueued) {
            power *= 0.1F;
        }

        power *= 1.0 - instance.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (power > 0.0) {
            instance.needsSync = true;
            Vec3 deltaMovement = instance.getDeltaMovement();

            while (xd * xd + zd * zd < 1.0E-5F) {
                xd = (Math.random() - Math.random()) * 0.01;
                zd = (Math.random() - Math.random()) * 0.01;
            }

            Vec3 vec32 = new Vec3(xd, 0.0, zd).normalize().scale(power);
            if (instance.onGround()) {
                instance.setDeltaMovement(
                        deltaMovement.x / 2.0 - vec32.x,
                        Math.min(0.5, deltaMovement.y / 2.0 + power),
                        deltaMovement.z / 2.0 - vec32.z
                );
            } else {
                instance.setDeltaMovement(
                        deltaMovement.x / 2.5 - vec32.x,
                        Math.min(0.5, deltaMovement.y / 2.5 + power * 0.8),
                        deltaMovement.z / 2.5 - vec32.z
                );
            }
        }

        ci.cancel();
    }

    @Inject(
            method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void damage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity instance = (LivingEntity) (Object) this;
        LivingEntityAccessor accessor = (LivingEntityAccessor) instance;
        LivingEntityInvoker invoker = (LivingEntityInvoker) instance;

        if (instance.isInvulnerableTo(world, source)) {
            cir.setReturnValue(false);
        } else if (instance.isDeadOrDying()) {
            cir.setReturnValue(false);
        } else if (source.is(DamageTypeTags.IS_FIRE) && instance.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            cir.setReturnValue(false);
        } else {
            if (instance.isSleeping()) {
                instance.stopSleeping();
            }

            instance.setNoActionTime(0);
            if (amount < 0.0F) {
                amount = 0.0F;
            }

            ItemStack itemStack = instance.getUseItem();
            float g = instance.applyItemBlocking(world, source, amount);
            amount -= g;
            boolean bl = g > 0.0F;



            if (source.is(DamageTypeTags.IS_FREEZING) && instance.is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                amount *= 5.0F;
            }

            if (source.is(DamageTypeTags.DAMAGES_HELMET) && !instance.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                invoker.invokedHurtHelmet(source, amount);
                amount *= 0.75F;
            }

            if (Float.isNaN(amount) || Float.isInfinite(amount)) {
                amount = Float.MAX_VALUE;
            }

            boolean bl2 = true;
            if (instance.invulnerableTime > 10.0F && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                if (amount <= accessor.getLastHurt()) {
                    cir.setReturnValue(false);
                    return;
                }

                invoker.invokedApplyDamage(world, source, amount - accessor.getLastHurt());
                accessor.setLastDamageTaken(amount);
                bl2 = false;
            } else {
                int phdi = 10;
                if (instance.level() instanceof ServerLevel serverWorld) {
                    phdi = serverWorld.getGameRules().get(ModGameRules.PHDI);
                }
                accessor.setLastDamageTaken(amount);
                instance.invulnerableTime = 2 * phdi;

                invoker.invokedApplyDamage(world, source, amount);

                instance.hurtDuration = phdi;
                instance.hurtTime = instance.hurtDuration;
            }

            invoker.invokedBecomeAngry(source);
            invoker.invokedSetAttackingPlayer(source);
            if (bl2) {
                BlocksAttacks blocksAttacksComponent = itemStack.get(DataComponents.BLOCKS_ATTACKS);
                if (bl && blocksAttacksComponent != null) {
                    blocksAttacksComponent.onBlocked(world, instance);
                } else {
                    world.broadcastDamageEvent(instance, source);
                }

                if (!source.is(DamageTypeTags.NO_IMPACT) && (!bl || amount > 0.0F)) {
                    instance.hurtMarked = true;
                }

                if (!source.is(DamageTypeTags.NO_KNOCKBACK)) {
                    double d = 0.0;
                    double e = 0.0;
                    if (source.getDirectEntity() instanceof Projectile projectileEntity) {
                        DoubleDoubleImmutablePair doubleDoubleImmutablePair = projectileEntity.calculateHorizontalHurtKnockbackDirection(instance, source);
                        d = -doubleDoubleImmutablePair.leftDouble();
                        e = -doubleDoubleImmutablePair.rightDouble();
                    } else if (source.getSourcePosition() != null) {
                        d = source.getSourcePosition().x() - instance.getX();
                        e = source.getSourcePosition().z() - instance.getZ();
                    }

                    instance.knockback(0.4F, d, e);

                    if (!bl) {
                        instance.indicateDamage(d, e);
                    }
                }
            }

            if (instance.isDeadOrDying()) {
                if (!invoker.invokedTryUseDeathProtector(source)) {
                    if (bl2) {
                        instance.makeSound(invoker.invokedGetDeathSound());
                        invoker.invokedPlayThornsSound(source);
                    }

                    instance.die(source);
                }
            } else if (bl2) {
                invoker.invokedPlayHurtSound(source);
                invoker.invokedPlayThornsSound(source);
            }

            boolean bl3 = !bl || amount > 0.0F;
            if (bl3) {
                accessor.setLastDamageSource(source);
                accessor.setLastDamageTime(instance.level().getGameTime());

                for (MobEffectInstance statusEffectInstance : instance.getActiveEffects()) {
                    statusEffectInstance.onMobHurt(world, instance, source, amount);
                }
            }

            if (instance instanceof ServerPlayer serverPlayerEntity) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayerEntity, source, amount, amount, bl);
                if (g > 0.0F && g < 3.4028235E37F) {
                    serverPlayerEntity.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
                }
            }

            if (source.getEntity() instanceof ServerPlayer serverPlayerEntity) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, instance, source, amount, amount, bl);
            }

            cir.setReturnValue(bl3);
        }
    }
}