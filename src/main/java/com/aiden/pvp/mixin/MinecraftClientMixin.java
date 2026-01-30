package com.aiden.pvp.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(
            method = "doAttack",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient instance = (MinecraftClient) (Object) this;
        if (instance.player != null && instance.player.isBlocking()) {
            cir.setReturnValue(false);
        }
    }
}
