package com.aiden.pvp.mixin;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.item_properties_conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConditionalItemModelProperties.class)
public class ConditionalItemModelPropertiesMixin {
    @Inject(
            method = "bootstrap",
            at = @At(value = "HEAD")
    )
    private static void bootstrap(CallbackInfo ci) {
        ConditionalItemModelProperties.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(PvP.MOD_ID, "fishing_rod/cast"), FishingRodCast.MAP_CODEC);
    }
}
