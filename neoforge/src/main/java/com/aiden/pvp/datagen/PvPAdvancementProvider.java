package com.aiden.pvp.datagen;

import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;

public class PvPAdvancementProvider extends AdvancementSubProvider {
    public PvPAdvancementProvider(BootstrapContext<net.minecraft.advancements.Advancement> output) {
        super(output);
    }

    @Override
    public void generate() {
        // no custom advancements yet
    }
}