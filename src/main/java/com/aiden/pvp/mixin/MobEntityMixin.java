package com.aiden.pvp.mixin;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobEntityMixin {
    @Inject(
            method = "mobInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        PlayerEntityPvpExtension playerPvpExtension = (PlayerEntityPvpExtension) player;
        if (playerPvpExtension.isBlocking()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
