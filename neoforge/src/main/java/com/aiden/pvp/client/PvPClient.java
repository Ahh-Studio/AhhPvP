package com.aiden.pvp.client;

import com.aiden.pvp.client.keybinding.ModKeyBindings;
import com.aiden.pvp.client.render.entity.ChickenDefenseEntityRenderer;
import com.aiden.pvp.client.render.entity.DaggerEntityRenderer;
import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.client.render.entity.MurdererEntityRenderer;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import com.aiden.pvp.screen.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

public class PvPClient {
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyBindings.pvpKeyCategory = new KeyMapping.Category(Identifier.fromNamespaceAndPath(com.aiden.pvp.PvP.MOD_ID, "pvp"));
        ModKeyBindings.throwTntKeyBinding = new KeyMapping("key.pvp.throw_tnt", GLFW.GLFW_MOUSE_BUTTON_LEFT, ModKeyBindings.pvpKeyCategory);
        ModKeyBindings.openSettingsKeyBinding = new KeyMapping("key.pvp.open_settings", GLFW.GLFW_KEY_F7, ModKeyBindings.pvpKeyCategory);

        event.registerCategory(ModKeyBindings.pvpKeyCategory);
        event.register(ModKeyBindings.throwTntKeyBinding);
        event.register(ModKeyBindings.openSettingsKeyBinding);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ModKeyBindings.throwTntKeyBinding.consumeClick()) {
            if (Minecraft.getInstance().player != null) {
                ClientPacketDistributor.sendToServer(new ThrowTntC2SPayload(Minecraft.getInstance().player.getId()));
            }
        }
        while (ModKeyBindings.openSettingsKeyBinding.consumeClick()) {
            Minecraft client = Minecraft.getInstance();
            SettingsScreen settingsScreen = new SettingsScreen(null);
            if (client.screen == null) {
                client.setScreen(settingsScreen);
                break;
            }
            if (client.screen instanceof SettingsScreen) {
                client.setScreen(null);
            }
        }
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.FIREBALL, context -> new ThrownItemRenderer<>(context, 3.0F, true));
        event.registerEntityRenderer(ModEntityTypes.BRIDGE_EGG, context -> new ThrownItemRenderer<>(context, 1.0F, true));
        event.registerEntityRenderer(ModEntityTypes.BED_BUG, context -> new ThrownItemRenderer<>(context, 1.0F, true));
        event.registerEntityRenderer(ModEntityTypes.FISHING_BOBBER, FishingBobberEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.EGGLLET, ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.DAGGER, DaggerEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.MURDERER, MurdererEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CHICKEN_DEFENSE, ChickenDefenseEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ModEntityModelLayers.registerLayerDefinitions(event);
    }
}