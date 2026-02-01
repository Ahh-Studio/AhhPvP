package com.aiden.pvp.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(
            method = "startAttack",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        Minecraft instance = (Minecraft) (Object) this;
        if (instance.player != null && instance.player.isBlocking()) {
            cir.setReturnValue(false);
        }
    }
}
