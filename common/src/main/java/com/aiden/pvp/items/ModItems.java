package com.aiden.pvp.items;

import com.aiden.pvp.ModRegistrationConfig;
import com.aiden.pvp.PvPConstants;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.entities.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ModItems {
    private ModItems() {} // prevent instantiation

    private static final int SHORT_INVISIBILITY_DURATION = 600;
    private static final int LONG_INVISIBILITY_DURATION = 12000;
    private static final int PVP_SWORD_ATTACK_DAMAGE = 3;
    private static final int PVP_SWORD_ATTACK_SPEED = 251;

    // Fields are initialized in initialize() to avoid static initializer issues in NeoForge
    public static Item FIREBALL;
    public static Item SELF_RES_PLATFORM;
    public static Item BRIDGE_EGG;
    public static Item FISHING_ROD;
    public static Item BED_BUG;
    public static Item RETURN_SCROLL;
    public static Item CHICKEN_DEFENSE;
    public static Item EGGLLET;

    public static Item CARBON_RUNE;
    public static Item IRON_RUNE;
    public static Item BOSS_KEY;
    public static Item BBU_UPGRADE_SMITHING_TEMPLATE;

    public static Item TNT;
    public static Item THROWABLE_TNT;
    public static Item STRONG_GLASS;
    public static Item WHITE_STRONG_GLASS;
    public static Item LIGHT_GRAY_STRONG_GLASS;
    public static Item GRAY_STRONG_GLASS;
    public static Item BLACK_STRONG_GLASS;
    public static Item BROWN_STRONG_GLASS;
    public static Item RED_STRONG_GLASS;
    public static Item ORANGE_STRONG_GLASS;
    public static Item YELLOW_STRONG_GLASS;
    public static Item LIME_STRONG_GLASS;
    public static Item GREEN_STRONG_GLASS;
    public static Item CYAN_STRONG_GLASS;
    public static Item LIGHT_BLUE_STRONG_GLASS;
    public static Item BLUE_STRONG_GLASS;
    public static Item PURPLE_STRONG_GLASS;
    public static Item MAGENTA_STRONG_GLASS;
    public static Item PINK_STRONG_GLASS;

    public static Item GOLDEN_HEAD;
    public static Item BOSS_SPAWNER;
    public static Item LANDMINE;

    public static Item WOODEN_SWORD;
    public static Item STONE_SWORD;
    public static Item IRON_SWORD;
    public static Item DIAMOND_SWORD;
    public static Item AXE_OF_MURDER;

    public static Item THROWABLE_DAGGER;

    public static Holder<Potion> SHORT_INVISIBILITY_POTION;
    public static Holder<Potion> LONG_INVISIBILITY_POTION;
    public static Holder<Potion> OP_KIT_SWIFT_POTION;
    public static Holder<Potion> OP_KIT_REGENERATION_POTION;

    public static Item MURDERER_SPAWN_EGG;

    public static CreativeModeTab PVP_ITEM_GROUP;

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        Identifier id = Identifier.fromNamespaceAndPath(PvPConstants.MOD_ID, name);
        if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
            return Registry.registerForHolder(BuiltInRegistries.POTION, id, potion);
        }
        // Create a non-registered holder for NeoForge; RegisterEvent will handle registration
        return Holder.direct(potion);
    }

    public static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties settings) {
        final ResourceKey<Item> registryKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(PvPConstants.MOD_ID, path));
        Item.Properties props = settings.setId(registryKey).rarity(Rarity.EPIC);
        Item item = factory.apply(props);
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
            return Registry.register(BuiltInRegistries.ITEM, registryKey, item);
        }
        return item;
    }

    private static Item registerBlock(final Block block, final BiFunction<Block, Item.Properties, Item> itemFactory, final Item.Properties properties) {
        @SuppressWarnings("deprecation")
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, block.builtInRegistryHolder().key().identifier());
        Item.Properties p = properties.setId(key);
        Item item = itemFactory.apply(block, p);
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
            return Registry.register(BuiltInRegistries.ITEM, key, item);
        }
        return item;
    }

    public static void initialize() {
        try {
            // Create creative mode tab first
            PVP_ITEM_GROUP = CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 6)
                    .icon(() -> new ItemStack(Items.FISHING_ROD))
                    .title(Component.translatable("itemGroup.pvp_mod"))
                    .build();

            // Register all items
            FIREBALL = register("fireball", FireballItem::new, new Item.Properties().stacksTo(64).fireResistant().useCooldown(0.1F));
            SELF_RES_PLATFORM = register("self-res_platform", SelfRescuePlatformItem::new, new Item.Properties().stacksTo(64).craftRemainder(Items.BLAZE_ROD));
            BRIDGE_EGG = register("bridge_egg", BridgeEggItem::new, new Item.Properties().stacksTo(64).fireResistant());
            FISHING_ROD = register("fishing_rod", FishingRodItem::new, new Item.Properties().stacksTo(1).fireResistant().durability(64));
            BED_BUG = register("bed_bug", BedBugItem::new, new Item.Properties().stacksTo(16));
            RETURN_SCROLL = register("return_scroll", ReturnScrollItem::new, new Item.Properties().stacksTo(1));
            CHICKEN_DEFENSE = register("chicken_defense", ChickenDefenseItem::new, new Item.Properties().stacksTo(64).fireResistant());
            EGGLLET = register("eggllet", Item::new, new Item.Properties().stacksTo(64).fireResistant());

            CARBON_RUNE = register("carbon_rune", CarbonRuneItem::new, new Item.Properties().stacksTo(64));
            IRON_RUNE = register("iron_rune", IronRuneItem::new, new Item.Properties().stacksTo(64));
            BOSS_KEY = register("boss_key", Item::new, new Item.Properties().stacksTo(64));
            BBU_UPGRADE_SMITHING_TEMPLATE = register("bbu_upgrade_smithing_template",
                    settings -> new SmithingTemplateItem(
                            Component.translatable("item.pvp.smithing_template.bbu_upgrade.applies_to").withStyle(ChatFormatting.BLUE),
                            Component.translatable("item.pvp.smithing_template.bbu_upgrade.ingredients").withStyle(ChatFormatting.BLUE),
                            Component.translatable("item.pvp.smithing_template.bbu_upgrade.base_slot_description"),
                            Component.translatable("item.pvp.smithing_template.bbu_upgrade.additions_slot_description"),
                            List.of(Identifier.withDefaultNamespace("container/slot/sword")),
                            List.of(Identifier.fromNamespaceAndPath(PvPConstants.MOD_ID, "container/slot/null")),
                            settings
                    ),
                    new Item.Properties()
            );

            // Block items
            TNT = registerBlock(ModBlocks.TNT, TntBlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.tnt"));
            THROWABLE_TNT = registerBlock(ModBlocks.THROWABLE_TNT, ThrowableTntBlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.throwable_tnt"));
            STRONG_GLASS = registerBlock(ModBlocks.STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.strong_glass"));
            WHITE_STRONG_GLASS = registerBlock(ModBlocks.WHITE_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.white_strong_glass"));
            LIGHT_GRAY_STRONG_GLASS = registerBlock(ModBlocks.LIGHT_GRAY_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.light_gray_strong_glass"));
            GRAY_STRONG_GLASS = registerBlock(ModBlocks.GRAY_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.gray_strong_glass"));
            BLACK_STRONG_GLASS = registerBlock(ModBlocks.BLACK_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.black_strong_glass"));
            BROWN_STRONG_GLASS = registerBlock(ModBlocks.BROWN_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.brown_strong_glass"));
            RED_STRONG_GLASS = registerBlock(ModBlocks.RED_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.red_strong_glass"));
            ORANGE_STRONG_GLASS = registerBlock(ModBlocks.ORANGE_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.orange_strong_glass"));
            YELLOW_STRONG_GLASS = registerBlock(ModBlocks.YELLOW_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.yellow_strong_glass"));
            LIME_STRONG_GLASS = registerBlock(ModBlocks.LIME_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.lime_strong_glass"));
            GREEN_STRONG_GLASS = registerBlock(ModBlocks.GREEN_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.green_strong_glass"));
            CYAN_STRONG_GLASS = registerBlock(ModBlocks.CYAN_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.cyan_strong_glass"));
            LIGHT_BLUE_STRONG_GLASS = registerBlock(ModBlocks.LIGHT_BLUE_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.light_blue_strong_glass"));
            BLUE_STRONG_GLASS = registerBlock(ModBlocks.BLUE_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.blue_strong_glass"));
            PURPLE_STRONG_GLASS = registerBlock(ModBlocks.PURPLE_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.purple_strong_glass"));
            MAGENTA_STRONG_GLASS = registerBlock(ModBlocks.MAGENTA_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.magenta_strong_glass"));
            PINK_STRONG_GLASS = registerBlock(ModBlocks.PINK_STRONG_GLASS, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.pink_strong_glass"));

            GOLDEN_HEAD = registerBlock(ModBlocks.GOLDEN_HEAD, GoldenHeadItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(2).overrideDescription("block.pvp.golden_head"));
            BOSS_SPAWNER = registerBlock(ModBlocks.BOSS_SPAWNER, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.boss_spawner"));
            LANDMINE = registerBlock(ModBlocks.LANDMINE, BlockItem::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(64).overrideDescription("block.pvp.landmine"));

            WOODEN_SWORD = register("wooden_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.WOOD, PVP_SWORD_ATTACK_DAMAGE, PVP_SWORD_ATTACK_SPEED));
            STONE_SWORD = register("stone_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.STONE, PVP_SWORD_ATTACK_DAMAGE, PVP_SWORD_ATTACK_SPEED));
            IRON_SWORD = register("iron_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.IRON, PVP_SWORD_ATTACK_DAMAGE, PVP_SWORD_ATTACK_SPEED));
            DIAMOND_SWORD = register("diamond_sword", SwordItem::new, new Item.Properties().sword(ToolMaterial.DIAMOND, PVP_SWORD_ATTACK_DAMAGE, PVP_SWORD_ATTACK_SPEED));
            AXE_OF_MURDER = register("axe_of_murder", properties -> new AxeItem(ModToolMaterials.SUPER, PVP_SWORD_ATTACK_DAMAGE, PVP_SWORD_ATTACK_SPEED, properties), new Item.Properties());

            THROWABLE_DAGGER = register("throwable_dagger", ThrowableDaggerItem::new, new Item.Properties().sword(ToolMaterial.IRON, 2, 251).useCooldown(5));

            // Potions must be registered before items that reference them
            SHORT_INVISIBILITY_POTION = registerPotion("short_invisibility_potion", new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, SHORT_INVISIBILITY_DURATION)));
            LONG_INVISIBILITY_POTION = registerPotion("long_invisibility_potion", new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, LONG_INVISIBILITY_DURATION)));
            OP_KIT_SWIFT_POTION = registerPotion("op_kit_swift", new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, 1200, 1), new MobEffectInstance(MobEffects.SPEED, 3600)));
            OP_KIT_REGENERATION_POTION = registerPotion("op_kit_regeneration", new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 900)));

            MURDERER_SPAWN_EGG = register("murderer_spawn_egg", SpawnEggItem::new, new Item.Properties().spawnEgg(ModEntityTypes.MURDERER));

            // Register creative mode tab
            if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
                Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(PvPConstants.MOD_ID, "assets/pvp")), PVP_ITEM_GROUP);
            }

            PvPConstants.LOGGER.info("[Item Initializer] Mod Items Initialized! ");
        } catch (Exception e) {
            PvPConstants.LOGGER.error("[Item Initializer] An Error Occurred: ", e);
            throw new RuntimeException("Failed to initialize ModItems", e);
        }
    }

    public static void registerBrewingRecipes(PotionBrewing.Builder builder) {
        builder.addMix(Potions.WATER, Items.GLASS, SHORT_INVISIBILITY_POTION);
        builder.addMix(Potions.WATER, STRONG_GLASS, LONG_INVISIBILITY_POTION);
    }
}