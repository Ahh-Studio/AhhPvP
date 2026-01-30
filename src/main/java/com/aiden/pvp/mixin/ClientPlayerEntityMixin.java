package com.aiden.pvp.mixin;

import com.aiden.pvp.mixin.invoker.ClientPlayerEntityInvoker;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(
            method = "applyMovementSpeedFactors",
            at = @At(
                value = "HEAD"
            ),
            cancellable = true
    )
    public void applyMovementSpeedFactors(Vec2f input, CallbackInfoReturnable<Vec2f> cir) {
        ClientPlayerEntity instance = (ClientPlayerEntity) (Object) this;
        ClientPlayerEntityInvoker invoker = (ClientPlayerEntityInvoker) instance;
        PlayerEntityPvpExtension instanceExt = (PlayerEntityPvpExtension) instance;

        if (input.lengthSquared() == 0.0F) {
            cir.setReturnValue(input);
        } else {
            Vec2f vec2f = input.multiply(0.98F);
            if (instance.isUsingItem() && !instance.hasVehicle()) {
                vec2f = vec2f.multiply(invoker.invokedGetActiveItemSpeedMultiplier());
            }

            if (instance.shouldSlowDown()) {
                float f = (float) instance.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
                vec2f = vec2f.multiply(f);
            }

            if (instanceExt.isBlocking() && !instance.getAbilities().flying) {
                vec2f = vec2f.multiply(0.5F);
            }

            cir.setReturnValue(invoker.invokedApplyDirectionalMovementSpeedFactors(vec2f));
        }
    }
}
