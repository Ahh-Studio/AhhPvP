package com.aiden.pvp.datagen;

import com.aiden.pvp.datagen.lang.PvPEnGbLangProvider;
import com.aiden.pvp.datagen.lang.PvPEnUsLangProvider;
import com.aiden.pvp.datagen.lang.PvPZhCnLangProvider;
import com.aiden.pvp.datagen.lang.PvPZhTwLangProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PvPDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(PvPRecipeProvider::new);
        pack.addProvider(PvPBlockLootTableProvider::new);
        pack.addProvider(PvPAdvancementProvider::new);
        pack.addProvider(PvPModelProvider::new);
        pack.addProvider(PvPEnUsLangProvider::new);
        pack.addProvider(PvPZhCnLangProvider::new);
        pack.addProvider(PvPZhTwLangProvider::new);
        pack.addProvider(PvPEnGbLangProvider::new);
    }
}
