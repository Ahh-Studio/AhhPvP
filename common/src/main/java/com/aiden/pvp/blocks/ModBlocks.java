package com.aiden.pvp.blocks;

import com.aiden.pvp.ModRegistrationConfig;
import com.aiden.pvp.PvP;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.function.Function;

class GlassBlock extends TransparentBlock {
    public GlassBlock(Properties properties) {
        super(properties);
    }
}

public class ModBlocks {
    public static Block SPECIAL_SLIME_BLOCK;
    public static Block EGG_BRIDGE;
    public static Block TNT;
    public static Block THROWABLE_TNT;
    public static Block STRONG_GLASS;
    public static Block WHITE_STRONG_GLASS;
    public static Block LIGHT_GRAY_STRONG_GLASS;
    public static Block GRAY_STRONG_GLASS;
    public static Block BLACK_STRONG_GLASS;
    public static Block BROWN_STRONG_GLASS;
    public static Block RED_STRONG_GLASS;
    public static Block ORANGE_STRONG_GLASS;
    public static Block YELLOW_STRONG_GLASS;
    public static Block LIME_STRONG_GLASS;
    public static Block GREEN_STRONG_GLASS;
    public static Block CYAN_STRONG_GLASS;
    public static Block LIGHT_BLUE_STRONG_GLASS;
    public static Block BLUE_STRONG_GLASS;
    public static Block PURPLE_STRONG_GLASS;
    public static Block MAGENTA_STRONG_GLASS;
    public static Block PINK_STRONG_GLASS;
    public static Block GOLDEN_HEAD;
    public static Block BOSS_SPAWNER;
    public static Block BOSS_BATTLE_HANDLER;
    public static Block LANDMINE;
    public static Block DEFENSE_TOWER;

    private static Block registerGlass(String name) {
        return register(name, GlassBlock::new, BlockBehaviour.Properties.of()
                .sound(SoundType.GLASS)
                .strength(0.3F, Float.MAX_VALUE)
                .noOcclusion()
                .instrument(NoteBlockInstrument.HAT)
                .isValidSpawn((state, level, pos, type) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
        );
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings) {
        Identifier id = Identifier.fromNamespaceAndPath(PvP.MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        BlockBehaviour.Properties props = settings.setId(blockKey);
        Block block = blockFactory.apply(props);
        if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
            return Registry.register(BuiltInRegistries.BLOCK, id, block);
        }
        return block;
    }

    public static void initialize() {
        try {
            SPECIAL_SLIME_BLOCK = register(
                    "slime_block",
                    SlimeBlock::new,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.GRASS)
                            .friction(0.8F)
                            .sound(SoundType.SLIME_BLOCK)
                            .noOcclusion()
            );
            EGG_BRIDGE = register(
                    "egg_bridge",
                    EggBridgeBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.WOOL)
                            .instabreak()
                            .noLootTable()
                            .destroyTime(0.01F)
            );
            TNT = register(
                    "tnt",
                    TntBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.GRASS)
                            .instabreak()
                            .destroyTime(0.01F)
            );
            THROWABLE_TNT = register(
                    "throwable_tnt",
                    TntBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.GRASS)
                            .instabreak()
                            .destroyTime(0.01F)
            );
            STRONG_GLASS = registerGlass("strong_glass");
            WHITE_STRONG_GLASS = registerGlass("white_strong_glass");
            LIGHT_GRAY_STRONG_GLASS = registerGlass("light_gray_strong_glass");
            GRAY_STRONG_GLASS = registerGlass("gray_strong_glass");
            BLACK_STRONG_GLASS = registerGlass("black_strong_glass");
            BROWN_STRONG_GLASS = registerGlass("brown_strong_glass");
            RED_STRONG_GLASS = registerGlass("red_strong_glass");
            ORANGE_STRONG_GLASS = registerGlass("orange_strong_glass");
            YELLOW_STRONG_GLASS = registerGlass("yellow_strong_glass");
            LIME_STRONG_GLASS = registerGlass("lime_strong_glass");
            GREEN_STRONG_GLASS = registerGlass("green_strong_glass");
            CYAN_STRONG_GLASS = registerGlass("cyan_strong_glass");
            LIGHT_BLUE_STRONG_GLASS = registerGlass("light_blue_strong_glass");
            BLUE_STRONG_GLASS = registerGlass("blue_strong_glass");
            PURPLE_STRONG_GLASS = registerGlass("purple_strong_glass");
            MAGENTA_STRONG_GLASS = registerGlass("magenta_strong_glass");
            PINK_STRONG_GLASS = registerGlass("pink_strong_glass");
            GOLDEN_HEAD = register(
                    "golden_head",
                    GoldenHeadBlock::new,
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.CUSTOM_HEAD)
                            .strength(1200.0F)
                            .pushReaction(PushReaction.BLOCK)
            );
            BOSS_SPAWNER = register(
                    "boss_spawner",
                    BossSpawnerBlock::new,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(0)
                            .sound(SoundType.ANVIL)
                            .instabreak()
            );
            BOSS_BATTLE_HANDLER = register(
                    "boss_battle_handler",
                    BossBattleHandlerBlock::new,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(0)
                            .sound(SoundType.ANVIL)
                            .instabreak()
            );
            LANDMINE = register(
                    "landmine",
                    LandmineBlock::new,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
            );
            DEFENSE_TOWER = register(
                    "defense_tower",
                    DefenseTowerBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
            );

            PvP.LOGGER.info("[Block Initializer] Mod Blocks Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.error("[Block Initializer] An Error Occurred: ", e);
            throw new RuntimeException("Failed to initialize ModBlocks", e);
        }
    }
}