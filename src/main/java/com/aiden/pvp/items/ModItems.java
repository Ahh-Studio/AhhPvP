package com.aiden.pvp.items;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.entities.ModEntityTypes;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;
import java.util.function.Function;

public abstract class ModItems {
    public static final Item FIREBALL = register("fireball", FireballItem::new, new Item.Properties().stacksTo(64).fireResistant().useCooldown(0.1F));
    public static final Item SELF_RES_PLATFORM = register("self-res_platform", SelfRescuePlatformItem::new, new Item.Properties().stacksTo(64).craftRemainder(Items.BLAZE_ROD));
    public static final Item BRIDGE_EGG = register("bridge_egg", BridgeEggItem::new, new Item.Properties().stacksTo(64).fireResistant());
    public static final Item FISHING_ROD = register("fishing_rod", FishingRodItem::new, new Item.Properties().stacksTo(1).fireResistant().durability(64));
    public static final Item BED_BUG = register("bed_bug", BedBugItem::new, new Item.Properties().stacksTo(16));
    public static final Item RETURN_SCROLL = register("return_scroll", ReturnScrollItem::new, new Item.Properties().stacksTo(1));
    public static final Item CHICKEN_DEFENSE = register("chicken_defense", ChickenDefenseItem::new, new Item.Properties().stacksTo(64).fireResistant());
    public static final Item EGGLLET = register("eggllet", Item::new, new Item.Properties().stacksTo(64).fireResistant());

    public static final Item CARBON_RUNE = register("carbon_rune", CarbonRuneItem::new, new Item.Properties().stacksTo(64));
    public static final Item IRON_RUNE = register("iron_rune", IronRuneItem::new, new Item.Properties().stacksTo(64));
    public static final Item BOSS_KEY = register("boss_key", Item::new, new Item.Properties().stacksTo(64));
    public static final Item BBU_UPGRADE_SMITHING_TEMPLATE = register("bbu_upgrade_smithing_template",
            settings -> new SmithingTemplateItem(
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.applies_to").withStyle(ChatFormatting.BLUE),
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.ingredients").withStyle(ChatFormatting.BLUE),
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.base_slot_description"),
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.additions_slot_description"),
                    List.of(Identifier.withDefaultNamespace("container/slot/sword")),
                    List.of(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "container/slot/null")),
                    settings
            ),
            new Item.Properties()
    );

    public static final Item TNT = Items.registerBlock(ModBlocks.TNT, TntBlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.tnt"));
    public static final Item THROWABLE_TNT = Items.registerBlock(ModBlocks.THROWABLE_TNT, ThrowableTntBlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.throwable_tnt"));
    public static final Item STRONG_GLASS = Items.registerBlock(ModBlocks.STRONG_GLASS, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.strong_glass"));
    public static final Item GOLDEN_HEAD = Items.registerBlock(ModBlocks.GOLDEN_HEAD, GoldenHeadItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(2).overrideDescription("block.pvp.golden_head"));
    public static final Item BOSS_SPAWNER = Items.registerBlock(ModBlocks.BOSS_SPAWNER, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.boss_spawner"));

    public static final Item WOODEN_SWORD = register("wooden_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.WOOD, 3, 251));
    public static final Item STONE_SWORD = register("stone_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.STONE, 3, 251));
    public static final Item IRON_SWORD = register("iron_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.IRON, 3, 251));
    public static final Item DIAMOND_SWORD = register("diamond_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.DIAMOND, 3, 251));

    public static final Item THROWABLE_DAGGER = register("throwable_dagger", ThrowableDaggerItem::new, new Item.Properties().sword(ToolMaterial.IRON, 2, 251).useCooldown(5));

    public static final Potion SHORT_INVISIBILITY_POTION = Registry.register(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(PvP.MOD_ID, "short_invisibility_potion"), new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0)));
    public static final Potion LONG_INVISIBILITY_POTION = Registry.register(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(PvP.MOD_ID, "long_invisibility_potion"), new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 12000, 0)));

    public static final Item MURDERER_SPAWN_EGG = register("murderer_spawn_egg", SpawnEggItem::new, new Item.Properties().spawnEgg(ModEntityTypes.MURDERER));

    public static final CreativeModeTab PVP_ITEM_GROUP = CreativeModeTab
            .builder(CreativeModeTab.Row.BOTTOM, 6)
            .icon(() -> new ItemStack(Items.FISHING_ROD))
            .title(Component.translatable("itemGroup.pvp_mod"))
            .build();

    public static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties settings) {
        final ResourceKey<Item> registryKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(PvP.MOD_ID, path));
        return Items.registerItem(registryKey, factory, settings.overrideDescription("item.pvp." + path).rarity(Rarity.EPIC));
    }

    public static void initialize() {
        try {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(PvP.MOD_ID, "assets/pvp")), PVP_ITEM_GROUP);

            PotionBrewing.Builder.BUILD.register(builder -> {
                builder.addMix(
                        Potions.WATER,
                        Items.GLASS,
                        BuiltInRegistries.POTION.wrapAsHolder(SHORT_INVISIBILITY_POTION)
                );
                builder.addMix(
                        Potions.WATER,
                        STRONG_GLASS,
                        BuiltInRegistries.POTION.wrapAsHolder(LONG_INVISIBILITY_POTION)
                );
            });

            ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(PvP.MOD_ID, "assets/pvp"))).register(i -> {
                i.accept(ModItems.FIREBALL);
                i.accept(ModItems.SELF_RES_PLATFORM);
                i.accept(ModItems.BRIDGE_EGG);
                i.accept(ModItems.FISHING_ROD);
                i.accept(ModItems.BED_BUG);
                i.accept(ModItems.RETURN_SCROLL);
                i.accept(ModItems.CHICKEN_DEFENSE);
                i.accept(ModItems.TNT);
                i.accept(ModItems.THROWABLE_TNT);
                i.accept(ModItems.STRONG_GLASS);
                i.accept(ModItems.BOSS_SPAWNER);
                i.accept(ModItems.BOSS_KEY);
                i.accept(ModItems.GOLDEN_HEAD);
                i.accept(ModItems.CARBON_RUNE);
                i.accept(ModItems.IRON_RUNE);
                i.accept(ModItems.WOODEN_SWORD);
                i.accept(ModItems.STONE_SWORD);
                i.accept(ModItems.IRON_SWORD);
                i.accept(ModItems.DIAMOND_SWORD);
                i.accept(ModItems.THROWABLE_DAGGER);
                i.accept(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE);
                i.accept(ModItems.MURDERER_SPAWN_EGG);
                i.getContext().holders().lookup(Registries.POTION).ifPresent(potion -> potion.listElements()
                        .filter(potionReference -> potionReference.value().isEnabled(i.getEnabledFeatures()) && (potionReference.value() == LONG_INVISIBILITY_POTION || potionReference.value() == SHORT_INVISIBILITY_POTION))
                        .map(potionReference -> PotionContents.createItemStack(Items.POTION, potionReference))
                        .forEach(itemStack -> i.accept(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)));
            });
            ItemTooltipCallback.EVENT.register((i, context, type, list) -> {
                if (i.is(FIREBALL)) list.add(Component.literal("Use it... and watch it explode!"));
                if (i.is(SELF_RES_PLATFORM)) list.add(Component.literal("Breaking the fall!"));
                if (i.getItem() instanceof SwordItem) list.add(Component.literal("No attack CD"));
                if (i.is(GOLDEN_HEAD)) list.add(Component.literal("Powerful! "));
                if (i.is(RETURN_SCROLL)) {
                    list.add(Component.literal("Teleport you to your spawn point"));
                    list.add(Component.literal("It won't teleport you to the").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                    list.add(Component.literal("right place in the wrong dimension!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                }
            });
            PvP.LOGGER.info("[Item Initializer] Mod Items Initialized! ");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Item Initializer]  An Error Occurred! ");
        }
    }
}
