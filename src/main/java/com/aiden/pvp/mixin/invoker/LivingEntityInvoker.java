package com.aiden.pvp.mixin.invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
    @Invoker("applyDamage")
    void invokedApplyDamage(ServerWorld world, DamageSource source, float amount);

    @Invoker("becomeAngry")
    void invokedBecomeAngry(DamageSource damageSource);

    @Invoker("setAttackingPlayer")
    PlayerEntity invokedSetAttackingPlayer(DamageSource damageSource);

    @Invoker("tryUseDeathProtector")
    boolean invokedTryUseDeathProtector(DamageSource source);

    @Invoker("getDeathSound")
    SoundEvent invokedGetDeathSound();

    @Invoker("playThornsSound")
    void invokedPlayThornsSound(DamageSource damageSource);

    @Invoker("playHurtSound")
    void invokedPlayHurtSound(DamageSource source);
}
