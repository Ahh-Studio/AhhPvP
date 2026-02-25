package com.aiden.pvp.client.keybinding;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModKeyBindings {
    public static KeyMapping throwTntKeyBinding;
    public static KeyMapping openSettingsKeyBinding;
    public static KeyMapping.Category pvpKeyCategory;
}
