package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.PvP;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class ModEntityModelLayers {
    public static final ModelLayerLocation MURDERER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "murderer"),
            "main"
    );

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(MURDERER, MurdererEntityModel::getTexturedModelData);
    }
}
