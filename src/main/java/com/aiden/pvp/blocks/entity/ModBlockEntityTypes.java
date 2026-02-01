package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class ModBlockEntityTypes {

    public static BlockEntityType<? extends SlimeBlockEntity> SLIME_BLOCK_ENTITY;
    public static BlockEntityType<? extends BossSpawnerBlockEntity> BOSS_SPAWNER_BLOCK_ENTITY;
    public static BlockEntityType<? extends BossBattleHandlerBlockEntity> BOSS_BATTLE_HANDLER_BLOCK_ENTITY;

    @NotNull
    public static <T extends BlockEntity> BlockEntityType<? extends T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory,
            Block block)
    {
         return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(PvP.MOD_ID, name),
                FabricBlockEntityTypeBuilder.create(factory, block)
                        .addBlock(block)
                        .build()
         );
    }

    public static void initialize() {
        try {
            SLIME_BLOCK_ENTITY = register("slime_block_entity", SlimeBlockEntity::new, ModBlocks.SPECIAL_SLIME_BLOCK);
            BOSS_SPAWNER_BLOCK_ENTITY = register("boss_spawner_block_entity", BossSpawnerBlockEntity::new, ModBlocks.BOSS_SPAWNER);
            BOSS_BATTLE_HANDLER_BLOCK_ENTITY = register("boss_battle_handler_block_entity", BossBattleHandlerBlockEntity::new, ModBlocks.BOSS_BATTLE_HANDLER);
            PvP.LOGGER.info("[Block Entity Type Initializer] Mod Block Entity Types Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Block Entity Type Initializer] An Error Occurred: " + e.getMessage());
        }
    }
}
