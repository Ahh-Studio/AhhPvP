package com.aiden.pvp;

import com.aiden.pvp.ModRegistrationConfig;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.client.PvPClient;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.datagen.PvPDataGenerator;
import com.aiden.pvp.entities.ModEntityAttributes;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.payloads.handler.ClientPayloadHandler;
import com.aiden.pvp.payloads.handler.ServerPayloadHandler;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(PvP.MOD_ID)
public class PvP {
    public static final String MOD_ID = "pvp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public PvP(IEventBus modBus, ModContainer container) {
        // In NeoForge, all registrations must go through RegisterEvent/DeferredRegister,
        // so skip all Registry.register() calls in common static initializers.
        // Object creation (Item/Block/EntityType constructors) must happen inside
        // RegisterEvent callbacks where the registry is not yet frozen.
        ModRegistrationConfig.SKIP_REGISTRY_REGISTER = true;

        // Register the RegisterEvent listener (objects are created inside the callbacks)
        modBus.addListener(RegisterEvent.class, PvP::onRegister);

        // Network payload handlers
        modBus.addListener(ServerPayloadHandler::register);
        modBus.addListener(ClientPayloadHandler::register);

        // Entity attributes
        modBus.addListener(ModEntityAttributes::createAttributes);

        // Entity renderers
        modBus.addListener(PvPClient::registerEntityRenderers);
        modBus.addListener(PvPClient::registerLayerDefinitions);

        // Client setup
        NeoForge.EVENT_BUS.addListener(PvPClient::onClientTick);
        modBus.addListener(PvPClient::registerKeyMappings);

        // Data generator
        modBus.addListener(PvPDataGenerator::gatherData);

        // Common setup (dispenser behavior)
        modBus.addListener(PvP::commonSetupEventListener);

        // Creative tab content
        modBus.addListener(PvP::buildCreativeTabContents);

        // Client commands
        NeoForge.EVENT_BUS.addListener(ModCommands::onRegisterClientCommands);

        LOGGER.info("[Main] Mod Initialized Successfully! ");
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        // Register all blocks first (Items depend on Blocks for BlockItems)
        event.register(Registries.BLOCK, helper -> {
            ModBlocks.initialize();
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "slime_block"), ModBlocks.SPECIAL_SLIME_BLOCK);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "egg_bridge"), ModBlocks.EGG_BRIDGE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "tnt"), ModBlocks.TNT);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "throwable_tnt"), ModBlocks.THROWABLE_TNT);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "strong_glass"), ModBlocks.STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "white_strong_glass"), ModBlocks.WHITE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "light_gray_strong_glass"), ModBlocks.LIGHT_GRAY_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "gray_strong_glass"), ModBlocks.GRAY_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "black_strong_glass"), ModBlocks.BLACK_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "brown_strong_glass"), ModBlocks.BROWN_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "red_strong_glass"), ModBlocks.RED_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "orange_strong_glass"), ModBlocks.ORANGE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "yellow_strong_glass"), ModBlocks.YELLOW_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "lime_strong_glass"), ModBlocks.LIME_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "green_strong_glass"), ModBlocks.GREEN_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "cyan_strong_glass"), ModBlocks.CYAN_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "light_blue_strong_glass"), ModBlocks.LIGHT_BLUE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "blue_strong_glass"), ModBlocks.BLUE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "purple_strong_glass"), ModBlocks.PURPLE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "magenta_strong_glass"), ModBlocks.MAGENTA_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "pink_strong_glass"), ModBlocks.PINK_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "golden_head"), ModBlocks.GOLDEN_HEAD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "boss_spawner"), ModBlocks.BOSS_SPAWNER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "boss_battle_handler"), ModBlocks.BOSS_BATTLE_HANDLER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "landmine"), ModBlocks.LANDMINE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "defense_tower"), ModBlocks.DEFENSE_TOWER);
        });

        // Register all entity types (Items depend on EntityTypes for spawn eggs)
        event.register(Registries.ENTITY_TYPE, helper -> {
            ModEntityTypes.initialize();
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fireball"), ModEntityTypes.FIREBALL);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "bridge_egg"), ModEntityTypes.BRIDGE_EGG);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fishing_bobber"), ModEntityTypes.FISHING_BOBBER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "bed_bug"), ModEntityTypes.BED_BUG);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "dagger"), ModEntityTypes.DAGGER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "murderer"), ModEntityTypes.MURDERER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "chicken_defense"), ModEntityTypes.CHICKEN_DEFENSE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "eggllit"), ModEntityTypes.EGGLLET);
        });

        // Register all block entity types (depends on ModBlocks being initialized)
        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
            ModBlockEntityTypes.initialize();
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "slime_block_entity"), ModBlockEntityTypes.SLIME_BLOCK_ENTITY);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "boss_spawner_block_entity"), ModBlockEntityTypes.BOSS_SPAWNER_BLOCK_ENTITY);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "boss_battle_handler_block_entity"), ModBlockEntityTypes.BOSS_BATTLE_HANDLER_BLOCK_ENTITY);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "defense_tower_block_entity"), ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY);
        });

        // Register game rules
        event.register(Registries.GAME_RULE, helper -> {
            ModGameRules.initialize();
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "phdi"), ModGameRules.PHDI);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "shooter_affects_fireball_velocity"), ModGameRules.SHOOTER_AFFECTS_FIREBALL_VELOCITY);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fireball_shoot_power"), ModGameRules.FIREBALL_SHOOT_POWER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fireball_explode_power"), ModGameRules.FIREBALL_EXPLODE_POWER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fireball_creates_fire"), ModGameRules.FIREBALL_CREATES_FIRE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "self-res_platform_cd"), ModGameRules.SELF_RES_PLATFORM_CD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "self-res_platform_disappear_time"), ModGameRules.SELF_RES_PLATFORM_DISAPPEAR_TIME);
        });

        // Register all items (depends on ModBlocks and ModEntityTypes being initialized)
        event.register(Registries.ITEM, helper -> {
            ModItems.initialize();
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fireball"), ModItems.FIREBALL);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "self-res_platform"), ModItems.SELF_RES_PLATFORM);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "bridge_egg"), ModItems.BRIDGE_EGG);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "fishing_rod"), ModItems.FISHING_ROD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "bed_bug"), ModItems.BED_BUG);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "return_scroll"), ModItems.RETURN_SCROLL);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "chicken_defense"), ModItems.CHICKEN_DEFENSE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "eggllet"), ModItems.EGGLLET);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "carbon_rune"), ModItems.CARBON_RUNE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "iron_rune"), ModItems.IRON_RUNE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "boss_key"), ModItems.BOSS_KEY);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "bbu_upgrade_smithing_template"), ModItems.BBU_UPGRADE_SMITHING_TEMPLATE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "tnt"), ModItems.TNT);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "throwable_tnt"), ModItems.THROWABLE_TNT);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "strong_glass"), ModItems.STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "white_strong_glass"), ModItems.WHITE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "light_gray_strong_glass"), ModItems.LIGHT_GRAY_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "gray_strong_glass"), ModItems.GRAY_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "black_strong_glass"), ModItems.BLACK_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "brown_strong_glass"), ModItems.BROWN_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "red_strong_glass"), ModItems.RED_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "orange_strong_glass"), ModItems.ORANGE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "yellow_strong_glass"), ModItems.YELLOW_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "lime_strong_glass"), ModItems.LIME_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "green_strong_glass"), ModItems.GREEN_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "cyan_strong_glass"), ModItems.CYAN_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "light_blue_strong_glass"), ModItems.LIGHT_BLUE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "blue_strong_glass"), ModItems.BLUE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "purple_strong_glass"), ModItems.PURPLE_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "magenta_strong_glass"), ModItems.MAGENTA_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "pink_strong_glass"), ModItems.PINK_STRONG_GLASS);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "golden_head"), ModItems.GOLDEN_HEAD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "boss_spawner"), ModItems.BOSS_SPAWNER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "landmine"), ModItems.LANDMINE);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "wooden_sword"), ModItems.WOODEN_SWORD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "stone_sword"), ModItems.STONE_SWORD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "iron_sword"), ModItems.IRON_SWORD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "diamond_sword"), ModItems.DIAMOND_SWORD);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "axe_of_murder"), ModItems.AXE_OF_MURDER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "throwable_dagger"), ModItems.THROWABLE_DAGGER);
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "murderer_spawn_egg"), ModItems.MURDERER_SPAWN_EGG);
        });

        // Register all potions
        event.register(Registries.POTION, helper -> {
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "short_invisibility_potion"), ModItems.SHORT_INVISIBILITY_POTION.value());
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "long_invisibility_potion"), ModItems.LONG_INVISIBILITY_POTION.value());
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "op_kit_swift"), ModItems.OP_KIT_SWIFT_POTION.value());
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "op_kit_regeneration"), ModItems.OP_KIT_REGENERATION_POTION.value());
        });

        // Register creative mode tab
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "assets/pvp"), ModItems.PVP_ITEM_GROUP);
        });
    }

    @SubscribeEvent
    public static void commonSetupEventListener(FMLCommonSetupEvent event) {
        Item fireballItem = com.aiden.pvp.items.ModItems.FIREBALL;
        ProjectileDispenseBehavior projectileDispenserBehavior = new ProjectileDispenseBehavior(fireballItem);
        DispenserBlock.registerBehavior(fireballItem, projectileDispenserBehavior);
    }

    @SubscribeEvent
    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() != ModItems.PVP_ITEM_GROUP) return;
        event.accept(ModItems.FIREBALL);
        event.accept(ModItems.SELF_RES_PLATFORM);
        event.accept(ModItems.BRIDGE_EGG);
        event.accept(ModItems.FISHING_ROD);
        event.accept(ModItems.BED_BUG);
        event.accept(ModItems.RETURN_SCROLL);
        event.accept(ModItems.CHICKEN_DEFENSE);
        event.accept(ModItems.TNT);
        event.accept(ModItems.THROWABLE_TNT);
        event.accept(ModItems.STRONG_GLASS);
        event.accept(ModItems.WHITE_STRONG_GLASS);
        event.accept(ModItems.LIGHT_GRAY_STRONG_GLASS);
        event.accept(ModItems.GRAY_STRONG_GLASS);
        event.accept(ModItems.BLACK_STRONG_GLASS);
        event.accept(ModItems.BROWN_STRONG_GLASS);
        event.accept(ModItems.RED_STRONG_GLASS);
        event.accept(ModItems.ORANGE_STRONG_GLASS);
        event.accept(ModItems.YELLOW_STRONG_GLASS);
        event.accept(ModItems.LIME_STRONG_GLASS);
        event.accept(ModItems.GREEN_STRONG_GLASS);
        event.accept(ModItems.CYAN_STRONG_GLASS);
        event.accept(ModItems.LIGHT_BLUE_STRONG_GLASS);
        event.accept(ModItems.BLUE_STRONG_GLASS);
        event.accept(ModItems.PURPLE_STRONG_GLASS);
        event.accept(ModItems.MAGENTA_STRONG_GLASS);
        event.accept(ModItems.PINK_STRONG_GLASS);
        // 未开发完成
        // event.accept(ModItems.DEFENSE_TOWER);
        event.accept(ModItems.BOSS_SPAWNER);
        event.accept(ModItems.BOSS_KEY);
        event.accept(ModItems.GOLDEN_HEAD);
        event.accept(ModItems.CARBON_RUNE);
        event.accept(ModItems.IRON_RUNE);
        event.accept(ModItems.WOODEN_SWORD);
        event.accept(ModItems.STONE_SWORD);
        event.accept(ModItems.IRON_SWORD);
        event.accept(ModItems.DIAMOND_SWORD);
        event.accept(ModItems.THROWABLE_DAGGER);
        event.accept(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE);
        event.accept(ModItems.MURDERER_SPAWN_EGG);
        event.accept(ModItems.LANDMINE);
        event.accept(ModItems.AXE_OF_MURDER);
        BuiltInRegistries.POTION.listElements()
                .filter(potionReference -> potionReference.value().isEnabled(event.getFlags()) && (potionReference.is(ModItems.LONG_INVISIBILITY_POTION) || potionReference.is(ModItems.SHORT_INVISIBILITY_POTION)))
                .map(potionReference -> PotionContents.createItemStack(Items.POTION, potionReference))
                .forEach(event::accept);
    }
}