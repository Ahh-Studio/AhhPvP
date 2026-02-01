package com.aiden.pvp.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PvPAdvancementProvider extends FabricAdvancementProvider {
    public PvPAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.@NotNull Provider registryLookup, @NotNull Consumer<AdvancementHolder> consumer) {
        Advancements advancements = new Advancements();
        advancements.accept(consumer);
    }
}
