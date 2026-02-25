package com.aiden.pvp.mixin.accessor;

import net.minecraft.server.commands.DamageCommand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Stack;

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

    @Accessor("damageContainers")
    Stack<DamageContainer> getDamageContainers();

    @Accessor("damageContainers")
    void setDamageContainers(Stack<DamageContainer> damageContainers);
}
