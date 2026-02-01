package com.aiden.pvp.mixin.accessor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    float getLastHurt();

    @Accessor("lastHurt")
    void setLastDamageTaken(float lastDamageTaken);

    @Accessor("lastDamageSource")
    void setLastDamageSource(DamageSource lastDamageSource);

    @Accessor("lastDamageStamp")
    void setLastDamageTime(long lastDamageTime);
}
