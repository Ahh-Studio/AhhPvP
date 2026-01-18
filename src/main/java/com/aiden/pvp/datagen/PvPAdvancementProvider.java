package com.aiden.pvp.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PvPAdvancementProvider extends FabricAdvancementProvider {
    public PvPAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.@NotNull WrapperLookup registryLookup, @NotNull Consumer<AdvancementEntry> consumer) {
        Advancements advancements = new Advancements();
        advancements.accept(consumer);
    }
}
