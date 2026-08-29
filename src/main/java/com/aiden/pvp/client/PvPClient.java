package com.aiden.pvp.client;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.client.render.entity.ChickenDefenseEntityRenderer;
import com.aiden.pvp.client.render.entity.DaggerEntityRenderer;
import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.client.render.entity.MurdererEntityRenderer;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import com.aiden.pvp.screen.SettingsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import org.lwjgl.glfw.GLFW;

import static com.aiden.pvp.client.keybinding.ModKeyBindings.*;

@Environment(EnvType.CLIENT)
public class PvPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityModelLayers.register();

        EntityRenderers.register(ModEntityTypes.FIREBALL, context -> new ThrownItemRenderer<>(context, 3.0F, true));
        EntityRenderers.register(ModEntityTypes.BRIDGE_EGG, context -> new ThrownItemRenderer<>(context, 1.0F, true));
        EntityRenderers.register(ModEntityTypes.BED_BUG, context -> new ThrownItemRenderer<>(context, 1.0F, true));
        EntityRenderers.register(ModEntityTypes.FISHING_BOBBER, FishingBobberEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.EGGLLET, ThrownItemRenderer::new);
        EntityRenderers.register(ModEntityTypes.DAGGER, DaggerEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.MURDERER, MurdererEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.CHICKEN_DEFENSE, ChickenDefenseEntityRenderer::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY, ChestRenderer::new);

        throwTntKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.pvp.throw_tnt", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, PVP_KEY_CATEGORY)
        );
        openSettingsKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.pvp.open_settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F7, PVP_KEY_CATEGORY)
        );

        ClientTickEvents.END_CLIENT_TICK.register(PvPClient::endClientTickEventListener);

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CROSSHAIR,
                Identifier.fromNamespaceAndPath(PvP.MOD_ID, "before_crosshair"),
                PvPClient::extractBeforeCrosshair
        );
    }

    private static void endClientTickEventListener(Minecraft minecraft) {
        while (throwTntKeyBinding.consumeClick()) {
            if (minecraft.player != null) {
                ClientPlayNetworking.send(new ThrowTntC2SPayload(minecraft.player.getId()));
            }
        }
        while (openSettingsKeyBinding.consumeClick()) {
            LocalPlayer player = minecraft.player;

            if (player == null || !player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) break;

            if (minecraft.screen == null) {
                SettingsScreen settingsScreen = new SettingsScreen(null);
                minecraft.setScreen(settingsScreen);
                break;
            } else if (minecraft.screen instanceof SettingsScreen) {
                minecraft.setScreen(null);
            }
        }
    }

    private static void extractBeforeCrosshair(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        PlayerEntityPvpExtension playerExt = (PlayerEntityPvpExtension) localPlayer;
        if (playerExt != null && playerExt.AhhPvP$isTeleportingUsingReturnScroll()) {
            String s = Component.translatable("item.pvp.return_scroll").getString() +
                    " Teleport Countdown: " +
                    Math.round(playerExt.AhhPvP$getReturnScrollTeleportCountDown() / 20F);
            graphics.text(minecraft.font, s, 0, 0, 0xFFFFFFFF);
        }
    }
}
