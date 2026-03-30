package com.aiden.pvp.util.enchant;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantmentUtil {
    public static ItemStack enchantItemStack(ServerLevel level, ItemStack stack, ResourceKey<Enchantment> enchantment, int lvl) {
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(level.registryAccess().lookupOrThrow(enchantment.registryKey()).getOrThrow(enchantment), lvl);
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        return stack;
    }
}
