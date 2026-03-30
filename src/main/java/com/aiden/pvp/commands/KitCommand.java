package com.aiden.pvp.commands;

import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.util.enchant.EnchantmentUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class KitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> d, CommandBuildContext c, Commands.CommandSelection s) {
        d.register(Commands.literal("kit").requires(css -> css.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && css.isPlayer())
                .executes(KitCommand::sendHelpMessage)
                .then(Commands.literal("classic").executes(KitCommand::giveClassicKit))
                .then(Commands.literal("op").executes(KitCommand::giveOPKit))
        );
    }

    private static int sendHelpMessage(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("=== /Kit Command ===").withStyle(ChatFormatting.GREEN));
        context.getSource().sendSystemMessage(Component.literal(" - classic").withStyle(ChatFormatting.GREEN));
        context.getSource().sendSystemMessage(Component.literal(" - op").withStyle(ChatFormatting.GREEN));
        context.getSource().sendSystemMessage(Component.literal("WARNING: THIS COMMAND WILL CLEAR YOUR INVENTORY! ").withStyle(ChatFormatting.RED));
        return 0;
    }

    private static int giveClassicKit(CommandContext<CommandSourceStack> context) {
        context.getSource().getServer().execute(() -> {
            ServerLevel serverLevel = context.getSource().getLevel();
            ServerPlayer serverPlayer = context.getSource().getPlayer();

            if (serverPlayer != null) {
                serverPlayer.getInventory().clearContent();
                serverPlayer.getSlot(0).set(ModItems.IRON_SWORD.getDefaultInstance());
                serverPlayer.getSlot(1).set(ModItems.FISHING_ROD.getDefaultInstance());
                serverPlayer.getSlot(2).set(Items.BOW.getDefaultInstance());
                serverPlayer.getSlot(9).set(new ItemStack(Items.ARROW, 5));
                serverPlayer.getSlot(100).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.IRON_BOOTS.getDefaultInstance(), Enchantments.PROTECTION, 2));
                serverPlayer.getSlot(101).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.IRON_LEGGINGS.getDefaultInstance(), Enchantments.PROTECTION, 2));
                serverPlayer.getSlot(102).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.IRON_CHESTPLATE.getDefaultInstance(), Enchantments.PROTECTION, 2));
                serverPlayer.getSlot(103).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.IRON_HELMET.getDefaultInstance(), Enchantments.PROTECTION, 2));

                serverPlayer.inventoryMenu.broadcastChanges();
                context.getSource().sendSuccess(() -> Component.literal("Classic kit's given to ").append(serverPlayer.getName()), false);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int giveOPKit(CommandContext<CommandSourceStack> context) {
        context.getSource().getServer().execute(() -> {
            ServerLevel serverLevel = context.getSource().getLevel();
            ServerPlayer serverPlayer = context.getSource().getPlayer();

            if (serverPlayer != null) {
                serverPlayer.getInventory().clearContent();
                serverPlayer.getSlot(0).set(EnchantmentUtil.enchantItemStack(serverLevel, ModItems.DIAMOND_SWORD.getDefaultInstance(), Enchantments.SHARPNESS, 5));
                serverPlayer.getSlot(1).set(ModItems.FISHING_ROD.getDefaultInstance());
                serverPlayer.getSlot(2).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.BOW.getDefaultInstance(), Enchantments.POWER, 4));
                ItemStack flint_and_steel = Items.FLINT_AND_STEEL.getDefaultInstance();
                flint_and_steel.setDamageValue(60);
                serverPlayer.getSlot(3).set(flint_and_steel);
                serverPlayer.getSlot(4).set(new ItemStack(Items.GOLDEN_APPLE, 6));
                serverPlayer.getSlot(5).set(PotionContents.createItemStack(Items.SPLASH_POTION, ModItems.OP_KIT_SWIFT_POTION));
                serverPlayer.getSlot(6).set(PotionContents.createItemStack(Items.SPLASH_POTION, ModItems.OP_KIT_SWIFT_POTION));
                serverPlayer.getSlot(7).set(PotionContents.createItemStack(Items.SPLASH_POTION, ModItems.OP_KIT_REGENERATION_POTION));
                serverPlayer.getSlot(8).set(PotionContents.createItemStack(Items.SPLASH_POTION, ModItems.OP_KIT_REGENERATION_POTION));
                serverPlayer.getSlot(9).set(new ItemStack(Items.ARROW, 20));
                serverPlayer.getSlot(100).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.DIAMOND_BOOTS.getDefaultInstance(), Enchantments.PROTECTION, 3));
                serverPlayer.getSlot(101).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.DIAMOND_LEGGINGS.getDefaultInstance(), Enchantments.PROTECTION, 3));
                serverPlayer.getSlot(102).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.DIAMOND_CHESTPLATE.getDefaultInstance(), Enchantments.PROTECTION, 4));
                serverPlayer.getSlot(103).set(EnchantmentUtil.enchantItemStack(serverLevel, Items.DIAMOND_HELMET.getDefaultInstance(), Enchantments.PROTECTION, 3));

                serverPlayer.inventoryMenu.broadcastChanges();
                context.getSource().sendSuccess(() -> Component.literal("OP kit's given to ").append(serverPlayer.getName()), false);
            }
        });

        return Command.SINGLE_SUCCESS;
    }


}
