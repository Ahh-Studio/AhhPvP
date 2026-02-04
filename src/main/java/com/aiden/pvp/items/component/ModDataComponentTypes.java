package com.aiden.pvp.items.component;


import com.aiden.pvp.PvP;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModDataComponentTypes {
    public static final DataComponentType<Boolean> IS_TELEPORTING = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "is_teleporting"),
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build()
    );
}
