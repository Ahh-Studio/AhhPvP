package com.aiden.pvp.commands;

import com.aiden.pvp.screen.SettingsScreen;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

@Environment(EnvType.CLIENT)
public class AhhpvpCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> d, CommandBuildContext bc) {
        d.register(ClientCommandManager.literal("ahh-pvp-mod")
                .requires(css -> css.getPlayer().hasPermissions(2))
                .executes(context -> {
                    Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new SettingsScreen(null)));
                    return Command.SINGLE_SUCCESS;
                }));
    }
}
