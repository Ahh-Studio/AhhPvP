package com.aiden.pvp.mixin;

import com.aiden.pvp.mixin.invoker.ClientPlayerEntityInvoker;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
    @Inject(
            method = "modifyInput",
            at = @At(
                value = "HEAD"
            ),
            cancellable = true
    )
    public void applyMovementSpeedFactors(Vec2 input, CallbackInfoReturnable<Vec2> cir) {
        LocalPlayer instance = (LocalPlayer) (Object) this;
        ClientPlayerEntityInvoker invoker = (ClientPlayerEntityInvoker) instance;
        PlayerEntityPvpExtension instanceExt = (PlayerEntityPvpExtension) instance;

        if (input.lengthSquared() == 0.0F) {
            cir.setReturnValue(input);
        } else {
            Vec2 vec2f = input.scale(0.98F);
            if (instance.isUsingItem() && !instance.isPassenger()) {
                vec2f = vec2f.scale(invoker.invokedGetActiveItemSpeedMultiplier());
            }

            if (instance.isMovingSlowly()) {
                float f = (float) instance.getAttributeValue(Attributes.SNEAKING_SPEED);
                vec2f = vec2f.scale(f);
            }

            if (instanceExt.isBlocking() && !instance.getAbilities().flying) {
                vec2f = vec2f.scale(0.5F);
            }

            cir.setReturnValue(invoker.invokedApplyDirectionalMovementSpeedFactors(vec2f));
        }
    }
}
