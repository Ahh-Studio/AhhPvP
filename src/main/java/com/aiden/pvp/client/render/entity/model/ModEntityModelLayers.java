package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.PvP;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModEntityModelLayers {
    public static final EntityModelLayer MURDERER = new EntityModelLayer(
            Identifier.of(PvP.MOD_ID, "murderer"),
            "main"
    );

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(MURDERER, MurdererEntityModel::getTexturedModelData);
    }
}
