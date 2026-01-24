package com.aiden.pvp.commands;

import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

public class ModCommands {
    @Environment(EnvType.CLIENT)
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> {
            commandDispatcher.register(
                    ClientCommandManager.literal("ahh-pvp-mod")
                            .executes(context -> {
                                MinecraftClient.getInstance().execute(() -> {
                                    MinecraftClient.getInstance().setScreen(
                                            new SettingsScreen(null)
                                    );
                                });
                                return 1;
                            })
            );
        }));
    }
}
