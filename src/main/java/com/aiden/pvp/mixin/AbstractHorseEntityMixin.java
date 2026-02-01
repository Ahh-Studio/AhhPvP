package com.aiden.pvp.mixin;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public class AbstractHorseEntityMixin {
    @Inject(
            method = "openCustomInventoryScreen",
            at = @At("HEAD"),
            cancellable = true
    )
    public void openInventory(Player player, CallbackInfo ci) {
        PlayerEntityPvpExtension playerPvpExtension = (PlayerEntityPvpExtension) player;
        if (playerPvpExtension.isBlocking()) {
            ci.cancel();
        }
    }
}
