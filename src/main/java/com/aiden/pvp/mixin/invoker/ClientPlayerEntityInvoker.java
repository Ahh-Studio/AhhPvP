package com.aiden.pvp.mixin.invoker;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface ClientPlayerEntityInvoker {
    @Invoker("itemUseSpeedMultiplier")
    float invokedGetActiveItemSpeedMultiplier();

    @Invoker("modifyInputSpeedForSquareMovement")
    Vec2 invokedApplyDirectionalMovementSpeedFactors(Vec2 vec2f);
}
