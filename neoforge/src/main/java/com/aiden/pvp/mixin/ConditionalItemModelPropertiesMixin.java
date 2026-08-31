package com.aiden.pvp.mixin;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.item_properties_conditional.FishingRodCast;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConditionalItemModelProperties.class)
public class ConditionalItemModelPropertiesMixin {
    @Final
    @Shadow
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ConditionalItemModelProperty>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At(value = "HEAD"))
    private static void bootstrap(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "fishing_rod/cast"), FishingRodCast.MAP_CODEC);
    }
}