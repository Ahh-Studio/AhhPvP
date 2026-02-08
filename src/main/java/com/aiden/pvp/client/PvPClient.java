package com.aiden.pvp.client;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.ChickenDefenseEntityRenderer;
import com.aiden.pvp.client.render.entity.DaggerEntityRenderer;
import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.client.render.entity.MurdererEntityRenderer;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import com.aiden.pvp.screen.SettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.aiden.pvp.blocks.ModBlocks.*;
import static com.aiden.pvp.client.keybinding.ModKeyBindings.*;

@Environment(EnvType.CLIENT)
public class PvPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityModelLayers.register();

        EntityRenderers.register(
                ModEntityTypes.FIREBALL, context ->
                new ThrownItemRenderer<>(context, 3.0F, true)
        );
        EntityRenderers.register(ModEntityTypes.BRIDGE_EGG, context ->
                new ThrownItemRenderer<>(context, 1.0F, true)
        );
        EntityRenderers.register(ModEntityTypes.BED_BUG, context ->
                new ThrownItemRenderer<>(context, 1.0F, true)
        );
        EntityRenderers.register(
                ModEntityTypes.FISHING_BOBBER,
                FishingBobberEntityRenderer::new
        );
        EntityRenderers.register(
                ModEntityTypes.EGGLLIT,
                ThrownItemRenderer::new
        );
        EntityRenderers.register(ModEntityTypes.DAGGER, DaggerEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.MURDERER, MurdererEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.CHICKEN_DEFENSE, ChickenDefenseEntityRenderer::new);

        BlockRenderLayerMap.putBlock(STRONG_GLASS, ChunkSectionLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(SPECIAL_SLIME_BLOCK, ChunkSectionLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(BOSS_SPAWNER, ChunkSectionLayer.TRANSLUCENT);

        pvpKeyCategory = new KeyMapping.Category(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "pvp"));

        throwTntKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                        "key.pvp.throw_tnt", InputConstants.Type.MOUSE,
                        GLFW.GLFW_MOUSE_BUTTON_LEFT, pvpKeyCategory
        ));
        openSettingsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                        "key.pvp.open_settings", InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_F7, pvpKeyCategory
        ));

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (throwTntKeyBinding.consumeClick()) {
                if (minecraftClient.player != null) {
                    ClientPlayNetworking.send(new ThrowTntC2SPayload(minecraftClient.player.getId()));
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
        });
    }
}
