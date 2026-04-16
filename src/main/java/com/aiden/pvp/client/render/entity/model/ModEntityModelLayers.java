package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.PvP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ModEntityModelLayers {
    public static final ModelLayerLocation MURDERER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(PvP.MOD_ID, "murderer"),
            "main"
    );
    public static final ModelLayerLocation CHICKEN_DEFENSE = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(PvP.MOD_ID, "chicken_defense"),
            "main"
    );

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(MURDERER, MurdererEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CHICKEN_DEFENSE, ChickenDefenseEntityModel::createBodyLayer);
    }
}
