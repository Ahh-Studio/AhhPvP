package com.aiden.pvp.datagen;

import com.aiden.pvp.datagen.lang.*;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

public class PvPDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(PvPRecipeProvider.Runner::new);
        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(PvPBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider
        ));
        event.createProvider((output, lookupProvider) -> new AdvancementProvider(output, lookupProvider,
                List.of(
                        new PvPAdvancementProvider()
                )
        ));
        event.createProvider(PvPModelProvider::new);
        event.createProvider(PvPEnUsLangProvider::new);
        event.createProvider(PvPEnGbLangProvider::new);
        event.createProvider(PvPZhCnLangProvider::new);
        event.createProvider(PvPZhTwLangProvider::new);
    }
}
