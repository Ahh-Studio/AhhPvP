package com.aiden.pvp.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Stack;

/**
 * NeoForge adds a damageContainers stack to LivingEntity, and actuallyHurt() peeks from it.
 * The common LivingEntityMixin cancels hurtServer() without pushing a DamageContainer,
 * causing EmptyStackException. This mixin pushes the container before the common mixin runs
 * and pops it when the method returns.
 */
@Mixin(LivingEntity.class)
public class LivingEntityNeoForgeMixin {
    @Shadow
    private Stack<DamageContainer> damageContainers;

    @Inject(
            method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD")
    )
    private void pushDamageContainer(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        damageContainers.push(new DamageContainer(source, amount));
    }

    @Inject(
            method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("RETURN")
    )
    private void popDamageContainer(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!damageContainers.isEmpty()) {
            damageContainers.pop();
        }
    }
}