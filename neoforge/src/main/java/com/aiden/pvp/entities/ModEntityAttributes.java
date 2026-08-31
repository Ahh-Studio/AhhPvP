package com.aiden.pvp.entities;

import com.aiden.pvp.PvP;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEntityAttributes {
    @SubscribeEvent
    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.MURDERER, MurdererEntity.createMurdererAttributes().build());
        event.put(ModEntityTypes.CHICKEN_DEFENSE, Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .build());
        PvP.LOGGER.info("[Entity Attributes] Registered entity attributes");
    }
}