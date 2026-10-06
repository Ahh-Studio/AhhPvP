package com.aiden.pvp.client.keybinding;

import com.aiden.pvp.PvP;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {
    public static KeyMapping throwTntKeyBinding;
    public static KeyMapping openSettingsKeyBinding;

    public static KeyMapping.Category PVP_KEY_CATEGORY;

    public static void register() {
        PVP_KEY_CATEGORY = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(PvP.MOD_ID, "pvp")
        );

        throwTntKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.pvp.throw_tnt", InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_LEFT, PVP_KEY_CATEGORY));
        openSettingsKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.pvp.open_settings", InputConstants.Type.KEYBOARD, InputConstants.KEY_F7, PVP_KEY_CATEGORY));
    }
}