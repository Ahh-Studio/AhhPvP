package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PvP.MOD_ID);

    public static Supplier<BlockEntityType<SlimeBlockEntity>> SLIME_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "slime_block_entity", () -> new BlockEntityType<>(SlimeBlockEntity::new, ModBlocks.SPECIAL_SLIME_BLOCK.get()));
    public static Supplier<BlockEntityType<BossSpawnerBlockEntity>> BOSS_SPAWNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "boss_spawner_block_entity", () -> new BlockEntityType<>(BossSpawnerBlockEntity::new, ModBlocks.BOSS_SPAWNER.get()));
    public static Supplier<BlockEntityType<BossBattleHandlerBlockEntity>> BOSS_BATTLE_HANDLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "boss_battle_handler_block_entity", () -> new BlockEntityType<>(BossBattleHandlerBlockEntity::new, ModBlocks.BOSS_BATTLE_HANDLER.get()));

    public static void initialize(IEventBus modBus) {
        try {
            BLOCK_ENTITY_TYPES.register(modBus);
            PvP.LOGGER.info("[Block Entity Type Initializer] Mod Block Entity Types Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Block Entity Type Initializer] An Error Occurred: {}", e.getMessage());
        }
    }
}
