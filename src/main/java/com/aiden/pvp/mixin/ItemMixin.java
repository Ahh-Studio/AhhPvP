package com.aiden.pvp.mixin;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    public void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        PlayerEntityPvpExtension playerPvpExtension = (PlayerEntityPvpExtension) user;
        if (playerPvpExtension.isBlocking()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
