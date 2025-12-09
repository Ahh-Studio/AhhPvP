package com.aiden.pvp.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    float getLastDamageTaken();

    @Accessor("lastDamageTaken")
    void setLastDamageTaken(float lastDamageTaken);

    @Accessor
    DamageSource getLastDamageSource();

    @Accessor("lastDamageSource")
    void setLastDamageSource(DamageSource lastDamageSource);

    @Accessor("lastDamageTime")
    void setLastDamageTime(long lastDamageTime);
}
