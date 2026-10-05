package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class PvPBlockLootTableProvider extends FabricBlockLootSubProvider {
    private static final Holder<ContextIntProvider> ONE = Holder.direct(new ConstantValue(1));

    public PvPBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        this.add(Blocks.SPAWNER, LootTable.lootTable().withPool(applyExplosionCondition(ModItems.BOSS_KEY, LootPool.lootPool()
                .setRolls(ONE)
                .add(LootItem.lootTableItem(ModItems.BOSS_KEY))))
        );
        this.add(Blocks.TRIAL_SPAWNER, LootTable.lootTable().withPool(applyExplosionCondition(ModItems.BOSS_KEY, LootPool.lootPool()
                .setRolls(ONE)
                .add(LootItem.lootTableItem(ModItems.BOSS_KEY))))
        );
    }

    @Override
    public @NonNull String getName() {
        return "BlockLootTable";
    }
}