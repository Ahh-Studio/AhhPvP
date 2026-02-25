package com.aiden.pvp.client;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.ChickenDefenseEntityRenderer;
import com.aiden.pvp.client.render.entity.DaggerEntityRenderer;
import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.client.render.entity.MurdererEntityRenderer;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import com.aiden.pvp.screen.SettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

import static com.aiden.pvp.blocks.ModBlocks.*;
import static com.aiden.pvp.client.keybinding.ModKeyBindings.*;

@Mod(value = PvP.MOD_ID, dist = Dist.CLIENT)
public class PvPClient {
    public PvPClient(FMLModContainer container, IEventBus modBus, Dist dist) {
        modBus.addListener(ModEntityModelLayers::registerLayerDefinitions);
        modBus.addListener(PvPClient::registerEntityRenderers);

        pvpKeyCategory = new KeyMapping.Category(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "pvp"));
        throwTntKeyBinding = new KeyMapping("key.pvp.throw_tnt", GLFW.GLFW_MOUSE_BUTTON_LEFT, pvpKeyCategory);
        openSettingsKeyBinding = new KeyMapping("key.pvp.open_settings", GLFW.GLFW_KEY_F7, pvpKeyCategory);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(pvpKeyCategory);
        event.register(throwTntKeyBinding);
        event.register(openSettingsKeyBinding);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (throwTntKeyBinding.consumeClick()) {
            if (Minecraft.getInstance().player != null) {
                ClientPacketDistributor.sendToServer(new ThrowTntC2SPayload(Minecraft.getInstance().player.getId()));
            }
        }
        while (openSettingsKeyBinding.consumeClick()) {
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
        EntityRenderers.register(ModEntityTypes.FIREBALL.get(), context -> new ThrownItemRenderer<>(context, 3.0F, true));
        EntityRenderers.register(ModEntityTypes.BRIDGE_EGG.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
        EntityRenderers.register(ModEntityTypes.BED_BUG.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
        EntityRenderers.register(ModEntityTypes.FISHING_BOBBER.get(), FishingBobberEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.EGGLLIT.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntityTypes.DAGGER.get(), DaggerEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.MURDERER.get(), MurdererEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.CHICKEN_DEFENSE.get(), ChickenDefenseEntityRenderer::new);
    }
}
