package com.aiden.pvp.client.keybinding;

import com.aiden.pvp.PvP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {
    public static KeyMapping throwTntKeyBinding;
    public static KeyMapping openSettingsKeyBinding;

    public static final KeyMapping.Category PVP_KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "pvp")
    );
}