package com.aiden.pvp.mixin;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CamelEntity.class)
public class CamelEntityMixin {
    @Inject(
            method = "openInventory",
            at = @At("HEAD"),
            cancellable = true
    )
    public void openInventory(PlayerEntity player, CallbackInfo ci) {
        PlayerEntityPvpExtension playerPvpExtension = (PlayerEntityPvpExtension) player;
        if (playerPvpExtension.isBlocking()) {
            ci.cancel();
        }
    }
}
