package com.aiden.pvp.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((AhhpvpCommand::register));
        CommandRegistrationCallback.EVENT.register(KitCommand::register);
    }
}
