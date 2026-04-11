package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.PvP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ModEntityModelLayers {
    public static final ModelLayerLocation MURDERER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "murderer"),
            "main"
    );
    public static final ModelLayerLocation CHICKEN_DEFENSE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "chicken_defense"),
            "main"
    );

    public static void register() {
        ModelLayerRegistry.registerModelLayer(MURDERER, MurdererEntityModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(CHICKEN_DEFENSE, ChickenDefenseEntityModel::createBodyLayer);
    }
}
