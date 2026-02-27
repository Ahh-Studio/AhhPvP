package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.PvP;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ModEntityModelLayers {
    public static final ModelLayerLocation MURDERER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "murderer"),
            "main"
    );
    public static final ModelLayerLocation CHICKEN_DEFENSE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "chicken_defense"),
            "main"
    );

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MURDERER, MurdererEntityModel::getTexturedModelData);
        event.registerLayerDefinition(CHICKEN_DEFENSE, ChickenDefenseEntityModel::createBodyLayer);
    }
}
