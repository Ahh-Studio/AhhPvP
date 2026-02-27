package com.aiden.pvp.items;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.entities.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public abstract class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PvP.MOD_ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, PvP.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PvP.MOD_ID);

    public static final DeferredItem<Item> FIREBALL = ITEMS.registerItem("fireball", FireballItem::new, props -> props.stacksTo(64).fireResistant().useCooldown(0.1F).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> SELF_RES_PLATFORM = ITEMS.registerItem("self-res_platform", SelfRescuePlatformItem::new, props -> props.stacksTo(64).craftRemainder(Items.BLAZE_ROD).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> BRIDGE_EGG = ITEMS.registerItem("bridge_egg", BridgeEggItem::new, props -> props.stacksTo(64).fireResistant().rarity(Rarity.EPIC));
    public static final DeferredItem<Item> FISHING_ROD = ITEMS.registerItem("fishing_rod", FishingRodItem::new, props -> props.stacksTo(1).fireResistant().durability(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> BED_BUG = ITEMS.registerItem("bed_bug", BedBugItem::new, props -> props.stacksTo(16).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> RETURN_SCROLL = ITEMS.registerItem("return_scroll", ReturnScrollItem::new, props -> props.stacksTo(1).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> CHICKEN_DEFENSE = ITEMS.registerItem("chicken_defense", ChickenDefenseItem::new, props -> props.stacksTo(64).fireResistant().rarity(Rarity.EPIC));
    public static final DeferredItem<Item> EGGLLET = ITEMS.registerItem("eggllet", Item::new, props -> props.stacksTo(64).fireResistant().rarity(Rarity.EPIC));

    public static final DeferredItem<Item> CARBON_RUNE = ITEMS.registerItem("carbon_rune", CarbonRuneItem::new, props -> props.stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> IRON_RUNE = ITEMS.registerItem("iron_rune", IronRuneItem::new, props -> props.stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> BOSS_KEY = ITEMS.registerItem("boss_key", Item::new, props -> props.stacksTo(64).rarity(Rarity.EPIC));
    public static final DeferredItem<Item> BBU_UPGRADE_SMITHING_TEMPLATE = ITEMS.registerItem("bbu_upgrade_smithing_template",
            settings -> new SmithingTemplateItem(
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.applies_to").withStyle(ChatFormatting.BLUE),
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.ingredients").withStyle(ChatFormatting.BLUE),
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.base_slot_description"),
                    Component.translatable("item.pvp.smithing_template.bbu_upgrade.additions_slot_description"),
                    List.of(Identifier.withDefaultNamespace("container/slot/sword")),
                    List.of(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "container/slot/null")),
                    settings
            ),
            props -> props.rarity(Rarity.EPIC)
    );

    public static final DeferredItem<BlockItem> TNT = ITEMS.registerItem("tnt", TntBlockItem::new, props -> props.stacksTo(64).rarity(Rarity.EPIC).useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> THROWABLE_TNT = ITEMS.registerItem("throwable_tnt", ThrowableTntBlockItem::new, props -> props.stacksTo(64).useBlockDescriptionPrefix().rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> STRONG_GLASS = ITEMS.registerSimpleBlockItem("strong_glass", ModBlocks.STRONG_GLASS, props -> props.rarity(Rarity.EPIC).stacksTo(64).useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> GOLDEN_HEAD = ITEMS.registerItem("golden_head", GoldenHeadItem::new, props -> props.rarity(Rarity.EPIC).stacksTo(2).rarity(Rarity.EPIC).useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> BOSS_SPAWNER = ITEMS.registerSimpleBlockItem("boss_spawner", ModBlocks.BOSS_SPAWNER, props -> props.rarity(Rarity.EPIC).stacksTo(64).useBlockDescriptionPrefix());

    public static final DeferredItem<Item> WOODEN_SWORD = ITEMS.registerItem("wooden_sword", properties -> new SwordItem(properties, ToolMaterial.WOOD), props -> props.rarity(Rarity.EPIC));
    public static final DeferredItem<Item> STONE_SWORD = ITEMS.registerItem("stone_sword", properties -> new SwordItem(properties, ToolMaterial.STONE), props -> props.rarity(Rarity.EPIC));
    public static final DeferredItem<Item> IRON_SWORD = ITEMS.registerItem("iron_sword", properties -> new SwordItem(properties, ToolMaterial.IRON), props -> props.rarity(Rarity.EPIC));
    public static final DeferredItem<Item> DIAMOND_SWORD = ITEMS.registerItem("diamond_sword", properties -> new SwordItem(properties, ToolMaterial.DIAMOND), props -> props.rarity(Rarity.EPIC));

    public static final DeferredItem<Item> THROWABLE_DAGGER = ITEMS.registerItem("throwable_dagger", ThrowableDaggerItem::new, props -> props.useCooldown(5).rarity(Rarity.EPIC));

    public static final Holder<Potion> SHORT_INVISIBILITY_POTION = POTIONS.register("short_invisibility_potion", identifier -> new Potion(identifier.getPath(), new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0)));
    public static final Holder<Potion> LONG_INVISIBILITY_POTION = POTIONS.register("long_invisibility_potion", identifier -> new Potion(identifier.getPath(), new MobEffectInstance(MobEffects.INVISIBILITY, 12000, 0)));

    public static final DeferredItem<Item> MURDERER_SPAWN_EGG = ITEMS.registerItem("murderer_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntityTypes.MURDERER.get()).rarity(Rarity.EPIC));

    public static final Supplier<CreativeModeTab> PVP_CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.register("pvp", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(Items.FISHING_ROD))
            .title(Component.translatable("itemGroup.pvp_mod"))
            .displayItems((parameters, i) -> {
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
                parameters.holders().lookup(Registries.POTION).ifPresent(potion -> potion.listElements()
                        .filter(potionReference -> potionReference.value().isEnabled(parameters.enabledFeatures()) &&
                                (potionReference.value() == LONG_INVISIBILITY_POTION.value() || potionReference.value() == SHORT_INVISIBILITY_POTION.value()))
                        .map(potionReference -> PotionContents.createItemStack(Items.POTION, potionReference))
                        .forEach(itemStack -> i.accept(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)));
            })
            .build());

    public static void initialize(IEventBus modBus) {
        try {
            ITEMS.register(modBus);
            POTIONS.register(modBus);
            CREATIVE_MODE_TABS.register(modBus);
            PvP.LOGGER.info("[Item Initializer] Mod Items Initialized! ");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Item Initializer] An Error Occurred! ");
        }
    }

    @SubscribeEvent
    public static void handleRegisterBrewingRecipesEvent(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addMix(Potions.WATER, Items.GLASS, SHORT_INVISIBILITY_POTION);
        builder.addMix(Potions.WATER, STRONG_GLASS.get(), LONG_INVISIBILITY_POTION);
    }

    @SubscribeEvent
    public static void handleItemToolTipEvent(ItemTooltipEvent event) {
        ItemStack i = event.getItemStack();
        List<Component> list = event.getToolTip();

        if (i.is(FIREBALL)) list.add(Component.literal("Use it... and watch it explode!"));
        if (i.is(SELF_RES_PLATFORM)) list.add(Component.literal("Breaking the fall!"));
        if (i.getItem() instanceof SwordItem) list.add(Component.literal("No attack CD"));
        if (i.is(GOLDEN_HEAD)) list.add(Component.literal("Powerful! "));
        if (i.is(RETURN_SCROLL)) {
            list.add(Component.literal("Teleport you to your spawn point"));
            list.add(Component.literal("It won't teleport you to the").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            list.add(Component.literal("right place in the wrong dimension!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }
    }
}
