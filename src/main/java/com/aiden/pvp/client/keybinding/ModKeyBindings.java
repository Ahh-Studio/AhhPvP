package com.aiden.pvp.client.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {
    public static KeyMapping throwTntKeyBinding;
    public static KeyMapping openSettingsKeyBinding;
    public static KeyMapping.Category pvpKeyCategory;
}
