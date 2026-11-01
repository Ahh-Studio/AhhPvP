package com.aiden.pvp.datagen;

import com.aiden.pvp.datagen.lang.PvPEnUsLangProvider;
import com.aiden.pvp.datagen.lang.PvPZhCnLangProvider;
import com.aiden.pvp.datagen.lang.PvPZhTwLangProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

public class PvPDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(PvPModelProvider::new);
        event.createProvider(PvPEnUsLangProvider::new);
        event.createProvider(PvPZhCnLangProvider::new);
        event.createProvider(PvPZhTwLangProvider::new);
    }

    /// 26.x 的 RecipeProvider 走 BootstrapContext 注册，必须挂在 GatherDataRegistryEntriesEvent 上，
    /// 否则 runData 不会产出 recipe/advancement，并且会把 src/generated 里的旧文件当成过期数据删掉。
    @SubscribeEvent
    public static void gatherRegistries(GatherDataRegistryEntriesEvent event) {
        event.recipe(PvPRecipeProvider::new);
    }
}