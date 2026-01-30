package com.aiden.pvp.mixin.invoker;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityInvoker {
    @Invoker("getActiveItemSpeedMultiplier")
    float invokedGetActiveItemSpeedMultiplier();

    @Invoker("applyDirectionalMovementSpeedFactors")
    Vec2f invokedApplyDirectionalMovementSpeedFactors(Vec2f vec2f);
}
