package com.aiden.pvp.items.component;


import com.aiden.pvp.PvP;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModDataComponentTypes {
    public static final DataComponentType<ReturnScrollComponent> RETURN_SCROLL = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(PvP.MOD_ID, "return_scroll"),
            DataComponentType.<ReturnScrollComponent>builder().persistent(ReturnScrollComponent.CODEC).build()
    );
}
