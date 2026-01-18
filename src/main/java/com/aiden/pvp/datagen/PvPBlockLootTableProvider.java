package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class PvPBlockLootTableProvider extends SimpleFabricLootTableProvider {
        public PvPBlockLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
                super(output, registryLookup, LootContextTypes.BLOCK);
        }

        public static final RegistryKey<LootTable> SPAWNER_BLOCK = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("blocks/spawner"));
        public static final RegistryKey<LootTable> TRIAL_SPAWNER_BLOCK = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("blocks/trial_spawner"));

        @Override
        public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
                lootTableBiConsumer.accept(SPAWNER_BLOCK, LootTable.builder()
                        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                                .with(ItemEntry.builder(ModItems.BOSS_KEY)
                                        .apply(
                                                SetCountLootFunction.builder(
                                                        ConstantLootNumberProvider
                                                                .create(1.0F))))));
                lootTableBiConsumer.accept(TRIAL_SPAWNER_BLOCK, LootTable.builder()
                        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                                .with(ItemEntry.builder(ModItems.BOSS_KEY)
                                        .apply(
                                                SetCountLootFunction.builder(
                                                        ConstantLootNumberProvider
                                                                .create(1.0F))))));
        }
}