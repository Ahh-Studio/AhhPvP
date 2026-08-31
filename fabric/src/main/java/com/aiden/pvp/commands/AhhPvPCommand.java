package com.aiden.pvp.commands;

import com.aiden.pvp.screen.SettingsScreen;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class AhhPvPCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                ClientCommands.literal("ahh-pvp-mod").executes(commandContext -> {
                    Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new SettingsScreen(null));
                    });
                    return 1;
                })
        );
    }
}
