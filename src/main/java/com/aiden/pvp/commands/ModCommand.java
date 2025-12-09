package com.aiden.pvp.commands;

import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ModCommand {
    @Environment(EnvType.CLIENT)
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> {
            commandDispatcher.register(
                    ClientCommandManager.literal("ahh-pvp-mod")
                            .executes(context -> {
                                MinecraftClient.getInstance().execute(() -> {
                                    if (MinecraftClient.getInstance().player != null) {
//                                        MinecraftClient.getInstance().setScreen(new SettingsScreen(Text.translatable("screen.pvp.settings")));
                                    }
                                });
                                return 1;
                            })
            );
        }));
    }
}
