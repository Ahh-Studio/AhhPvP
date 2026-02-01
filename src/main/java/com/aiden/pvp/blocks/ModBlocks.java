package com.aiden.pvp.blocks;

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

public class ModBlocks {
    public static final Block SPECIAL_SLIME_BLOCK = register(
            "slime_block",
            SlimeBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GRASS)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );
    public static final Block EGG_BRIDGE = register(
            "egg_bridge",
            EggBridgeBlock::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOL)
                    .instabreak()
                    .noLootTable()
                    .destroyTime(0.01F)
    );
    public static final Block TNT = register(
            "tnt",
            TntBlock::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.GRASS)
                    .instabreak()
                    .destroyTime(0.01F)
    );
    public static final Block THROWABLE_TNT = register(
            "throwable_tnt",
            TntBlock::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.GRASS)
                    .instabreak()
                    .destroyTime(0.01F)
    );
    public static final Block STRONG_GLASS = register(
            "strong_glass",
            TransparentBlock::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(0.3F, 2147483647.0F)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.HAT)
                    .isValidSpawn(Blocks::never)
                    .isRedstoneConductor(Blocks::never)
                    .isSuffocating(Blocks::never)
                    .isViewBlocking(Blocks::never)
    );
    public static final Block GOLDEN_HEAD = register(
            "golden_head",
            GoldenHeadBlock::new,
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.CUSTOM_HEAD)
                    .strength(1200.0F)
                    .pushReaction(PushReaction.BLOCK)
    );
    public static final Block BOSS_SPAWNER = register(
            "boss_spawner",
            BossSpawnerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0)
                    .sound(SoundType.ANVIL)
                    .instabreak()
    );
    public static final Block BOSS_BATTLE_HANDLER = register(
            "boss_battle_handler",
            BossBattleHandlerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0)
                    .sound(SoundType.ANVIL)
                    .instabreak()
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings) {
        Identifier id = Identifier.fromNamespaceAndPath(PvP.MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        // 注册方块
        return Registry.register(BuiltInRegistries.BLOCK, id, blockFactory.apply(settings.setId(blockKey)));
    }
    public static void initialize() {
        try {
            PvP.LOGGER.info("[Block Initializer] Mod Blocks Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Block Initializer] An Error Occurred: {}", e.getMessage());
        }
    }
}
