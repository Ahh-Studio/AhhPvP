package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class PvPBlockLootTableProvider extends SimpleFabricLootTableProvider {
    public PvPBlockLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.BLOCK);
    }

    public static final ResourceKey<LootTable> SPAWNER_BLOCK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/spawner"));
    public static final ResourceKey<LootTable> TRIAL_SPAWNER_BLOCK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/trial_spawner"));

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