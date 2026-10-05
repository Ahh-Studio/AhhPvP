package com.aiden.pvp.datagen;

import com.aiden.pvp.datagen.lang.PvPEnUsLangProvider;
import com.aiden.pvp.datagen.lang.PvPZhCnLangProvider;
import com.aiden.pvp.datagen.lang.PvPZhTwLangProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class PvPDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(PvPModelProvider::new);
        event.createProvider(PvPEnUsLangProvider::new);
        event.createProvider(PvPZhCnLangProvider::new);
        event.createProvider(PvPZhTwLangProvider::new);
    }
}