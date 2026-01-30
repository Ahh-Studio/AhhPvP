package com.aiden.pvp.client;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.DaggerEntityRenderer;
import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.client.render.entity.MurdererEntityRenderer;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.items.SwordItem;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.aiden.pvp.payloads.BlockHitC2SPayload;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import java.util.jar.Attributes;

import static com.aiden.pvp.blocks.ModBlocks.*;
import static com.aiden.pvp.client.keybinding.ModKeyBindings.*;

@Environment(EnvType.CLIENT)
public class PvPClient implements ClientModInitializer {
    private static final Identifier BLOCK_INDICATOR = Identifier.of(PvP.MOD_ID, "block_indicator");

    @Override
    public void onInitializeClient() {
        ModEntityModelLayers.register();

        EntityRendererFactories.register(
                ModEntityTypes.FIREBALL,  // 你的实体类型常量
                FlyingItemEntityRenderer::new  // 使用投掷物默认渲染器
        );
        EntityRendererFactories.register(ModEntityTypes.BRIDGE_EGG, context ->
                new FlyingItemEntityRenderer<>(context, 1.0F, true)
        );
        EntityRendererFactories.register(ModEntityTypes.BED_BUG, context ->
                new FlyingItemEntityRenderer<>(context, 1.0F, true)
        );
        EntityRendererFactories.register(
                ModEntityTypes.FISHING_BOBBER,
                FishingBobberEntityRenderer::new
        );
        EntityRendererFactories.register(ModEntityTypes.DAGGER, DaggerEntityRenderer::new);
        EntityRendererFactories.register(ModEntityTypes.MURDERER, MurdererEntityRenderer::new);

        BlockRenderLayerMap.putBlock(STRONG_GLASS, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(SPECIAL_SLIME_BLOCK, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(BOSS_SPAWNER, BlockRenderLayer.TRANSLUCENT);

        pvpKeyCategory = new KeyBinding.Category(Identifier.of(PvP.MOD_ID, "pvp"));

        throwTntKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        "key.pvp.throw_tnt", InputUtil.Type.MOUSE,
                        GLFW.GLFW_MOUSE_BUTTON_LEFT, pvpKeyCategory
        ));
        openSettingsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        "key.pvp.open_settings", InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_F7, pvpKeyCategory
        ));
        blockHitKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        "key.pvp.block_hit", InputUtil.Type.MOUSE,
                        GLFW.GLFW_MOUSE_BUTTON_RIGHT, pvpKeyCategory
        ));

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (throwTntKeyBinding.wasPressed()) {
                ClientPlayNetworking.send(new ThrowTntC2SPayload(minecraftClient.player.getId()));
            }
            while (openSettingsKeyBinding.wasPressed()) {
                MinecraftClient client = MinecraftClient.getInstance();
                SettingsScreen settingsScreen = new SettingsScreen(null);
                if (client.currentScreen == null) {
                    client.setScreen(settingsScreen);
                    break;
                }
                if (client.currentScreen instanceof SettingsScreen) {
                    client.setScreen(null);
                }
            }

            ClientPlayerEntity player = minecraftClient.player;
            if (player != null) {
                PlayerEntityPvpExtension playerExt = (PlayerEntityPvpExtension) player;
                if (player.getMainHandStack().getItem() instanceof SwordItem && blockHitKeyBinding.isPressed()) {
                    if (!playerExt.isBlocking()) {
                        playerExt.setBlocking(true);
                        ClientPlayNetworking.send(new BlockHitC2SPayload(player.getId(), true));
                    }
                } else {
                    if (playerExt.isBlocking()) {
                        playerExt.setBlocking(false);
                        ClientPlayNetworking.send(new BlockHitC2SPayload(player.getId(), false));
                    }
                }
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, BLOCK_INDICATOR, PvPClient::renderBlockIndicator);
    }

    private static void renderBlockIndicator(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null && clientPlayerEntity.getEntityWorld() != null) {
            PlayerEntityPvpExtension playerEntityPvpExtension = (PlayerEntityPvpExtension) clientPlayerEntity;

            if (playerEntityPvpExtension.isBlocking()) {
                context.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        "Blocking",
                        (context.getScaledWindowWidth() - MinecraftClient.getInstance().textRenderer.getWidth("Blocking")) / 2,
                        context.getScaledWindowHeight() / 2 - 20,
                        0xFFFFFFFF,
                        false
                );
            }
        }
    }
}
