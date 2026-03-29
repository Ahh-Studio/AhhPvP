package com.aiden.pvp.commands;

import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;

public class ModCommands {
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register(((d, bc) -> {
            AhhpvpCommand.register(d, bc);
        }));
        CommandRegistrationCallback.EVENT.register((d, c, s) -> {
            KitCommand.register(d, c, s);
        });
    }
}
