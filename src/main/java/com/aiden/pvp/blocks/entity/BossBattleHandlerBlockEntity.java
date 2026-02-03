package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.entities.MurdererEntity;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;

public class BossBattleHandlerBlockEntity extends BlockEntity {
    public ArrayList<Entity> players = new ArrayList<>();
    public ServerBossEvent bossBar = new ServerBossEvent(
            Component.literal("AhhPvP Boss"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.NOTCHED_20
    );

    public BossBattleHandlerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOSS_BATTLE_HANDLER_BLOCK_ENTITY, pos, state);
    }

    public <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T entity) {
        tick(world, pos, state, (BossBattleHandlerBlockEntity) entity);
    }

    private void tick(Level world, BlockPos pos, BlockState state, BossBattleHandlerBlockEntity blockEntity) {
        int x= pos.getX(); int y = pos.getY(); int z = pos.getZ();
        AABB box = new AABB(
                x + 25, y + 12, z + 25,
                x - 25, y, z - 25
        );
        ArrayList<Entity> murderers = (ArrayList<Entity>) world.getEntities(
                (Entity) null,
                box,
                entity -> entity instanceof MurdererEntity && entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 700
        );

        if (murderers.isEmpty()) {
            for (Entity player : players) {
                if (player instanceof ServerPlayer serverPlayer) {
                    this.bossBar.removePlayer(serverPlayer);
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                }
            }
            this.summonChest(world, pos, state, blockEntity);
            return;
        }

        int bossHP = 0;
        for (Entity entity : murderers) {
            if (entity instanceof MurdererEntity murderer) {
                bossHP = (int) (bossHP + murderer.getHealth());
                bossHP = (int) (bossHP + murderer.getAbsorptionAmount());
            }
        }

        this.bossBar.setProgress(bossHP <= 40 ? (float) (bossHP / 40.0) : 1);

        AABB box2 = new AABB(
                x + 25, y + 12, z + 25,
                x - 25, y, z - 25
        );

        for (Entity entity : players) {
            if (entity instanceof ServerPlayer serverPlayer) {
                if (entity.distanceToSqr(x, y, z) <= 700 && serverPlayer.position().y() - y <= 12 && serverPlayer.position().y() - y > 0) {
                    this.bossBar.addPlayer(serverPlayer);
                    serverPlayer.setGameMode(GameType.ADVENTURE);
                } else {
                    this.bossBar.removePlayer(serverPlayer);
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                }

                if (world.getBlockEntity(pos) != blockEntity) {
                    bossBar.removePlayer(serverPlayer);
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                    return;
                }
            }
        }

        this.players = (ArrayList<Entity>) world.getEntities(
                (Entity) null,
                box2,
                entity -> entity instanceof Player
        );
    }

    public void removeBossBarPlayers() {
        for (Entity player : this.players) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.bossBar.removePlayer(serverPlayer);
                serverPlayer.setGameMode(GameType.SURVIVAL);
            }
        }
    }

    private void summonChest(Level world, BlockPos pos, BlockState state, BossBattleHandlerBlockEntity blockEntity) {
        world.setBlock(pos.above(), Blocks.BARREL.defaultBlockState(), 6);
        if (world.getBlockEntity(pos.above()) instanceof BarrelBlockEntity barrelBlockEntity) {
            Identifier lootTableId = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_boss");
            ResourceKey<LootTable> lootTableRegistryKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
            barrelBlockEntity.setLootTable(lootTableRegistryKey, world.random.nextLong());
        }
        world.setBlock(pos.above().north(), Blocks.BARREL.defaultBlockState(), 6);
        if (world.getBlockEntity(pos.above().north()) instanceof BarrelBlockEntity barrelBlockEntity) {
            Identifier lootTableId = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_boss");
            ResourceKey<LootTable> lootTableRegistryKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
            barrelBlockEntity.setLootTable(lootTableRegistryKey, world.random.nextLong());
        }
        world.setBlock(pos.above().west(), Blocks.BARREL.defaultBlockState(), 6);
        if (world.getBlockEntity(pos.above().west()) instanceof BarrelBlockEntity barrelBlockEntity) {
            Identifier lootTableId = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_boss");
            ResourceKey<LootTable> lootTableRegistryKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
            barrelBlockEntity.setLootTable(lootTableRegistryKey, world.random.nextLong());
        }
        world.setBlock(pos.above().north().west(), Blocks.BARREL.defaultBlockState(), 6);
        if (world.getBlockEntity(pos.above().north().west()) instanceof BarrelBlockEntity barrelBlockEntity) {
            Identifier lootTableId = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_boss");
            ResourceKey<LootTable> lootTableRegistryKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
            barrelBlockEntity.setLootTable(lootTableRegistryKey, world.random.nextLong());
        }
        world.setBlock(pos, Blocks.JUNGLE_PLANKS.defaultBlockState(), 6);
    }
}
