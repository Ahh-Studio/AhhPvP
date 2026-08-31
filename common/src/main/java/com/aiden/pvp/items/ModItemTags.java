package com.aiden.pvp.items;

import com.aiden.pvp.PvPConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> EMPTY = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(PvPConstants.MOD_ID, "empty"));
}