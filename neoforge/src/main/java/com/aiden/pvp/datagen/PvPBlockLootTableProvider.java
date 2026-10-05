package com.aiden.pvp.datagen;

import com.aiden.pvp.PvP;
import com.aiden.pvp.items.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

import java.util.Set;
import java.util.stream.Collectors;

public class PvPBlockLootTableProvider extends BlockLootSubProvider {
    public static final ResourceKey<LootTable> SPAWNER_BLOCK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/spawner"));
    public static final ResourceKey<LootTable> TRIAL_SPAWNER_BLOCK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/trial_spawner"));

    public PvPBlockLootTableProvider(LootTableSubProvider.Context context) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, context);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(PvP.MOD_ID))
                .map(e -> (Block) e.getValue())
                .collect(Collectors.toList());
    }

    @Override
    protected void generate() {
        Holder<ContextIntProvider> ONE = Holder.direct(new ConstantValue(1));
        this.output.accept(SPAWNER_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ONE)
                        .add(LootItem.lootTableItem(ModItems.BOSS_KEY)
                                .apply(SetItemCountFunction.setCount(ONE)))));
        this.output.accept(TRIAL_SPAWNER_BLOCK, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ONE)
                        .add(LootItem.lootTableItem(ModItems.BOSS_KEY)
                                .apply(SetItemCountFunction.setCount(ONE)))));
    }
}