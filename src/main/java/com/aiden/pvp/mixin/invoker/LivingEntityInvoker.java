package com.aiden.pvp.mixin.invoker;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
    @Invoker("actuallyHurt")
    void invokedApplyDamage(ServerLevel world, DamageSource source, float amount);

    @Invoker("resolveMobResponsibleForDamage")
    void invokedBecomeAngry(DamageSource damageSource);

    @Invoker("resolvePlayerResponsibleForDamage")
    Player invokedSetAttackingPlayer(DamageSource damageSource);

    @Invoker("checkTotemDeathProtection")
    boolean invokedTryUseDeathProtector(DamageSource source);

    @Invoker("getDeathSound")
    SoundEvent invokedGetDeathSound();

    @Invoker("playSecondaryHurtSound")
    void invokedPlayThornsSound(DamageSource damageSource);

    @Invoker("playHurtSound")
    void invokedPlayHurtSound(DamageSource source);
}
