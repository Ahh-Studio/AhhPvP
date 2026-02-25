package com.aiden.pvp.datagen;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;
import java.util.function.BiConsumer;

public class PvPBlockLootTableProvider extends BlockLootSubProvider {
    public static final ResourceKey<LootTable> SPAWNER_BLOCK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/spawner"));
    public static final ResourceKey<LootTable> TRIAL_SPAWNER_BLOCK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/trial_spawner"));

    public PvPBlockLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    @Override
    protected void generate() {

    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        lootTableBiConsumer.accept(SPAWNER_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ModItems.BOSS_KEY)
                                .apply(
                                        SetItemCountFunction.setCount(
                                                ConstantValue
                                                        .exactly(1.0F))))));
        lootTableBiConsumer.accept(TRIAL_SPAWNER_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ModItems.BOSS_KEY)
                                .apply(
                                        SetItemCountFunction.setCount(
                                                ConstantValue
                                                        .exactly(1.0F))))));
    }
}