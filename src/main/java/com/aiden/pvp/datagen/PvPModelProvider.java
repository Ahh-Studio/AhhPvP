package com.aiden.pvp.datagen;

import com.aiden.pvp.PvP;
import com.aiden.pvp.items.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

public class PvPModelProvider extends ModelProvider {
    public PvPModelProvider(PackOutput output) {
        super(output, PvP.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.RETURN_SCROLL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.EGGLLET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHICKEN_DEFENSE.get(), ModelTemplates.FLAT_ITEM);
    }

    @Override
    public @NonNull String getName() {
        return "Model";
    }
}
