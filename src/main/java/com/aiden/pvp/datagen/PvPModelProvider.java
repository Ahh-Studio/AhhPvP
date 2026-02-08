package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import org.jspecify.annotations.NonNull;

public class PvPModelProvider extends FabricModelProvider {
    public PvPModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(ModItems.RETURN_SCROLL, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.EGGLLIT, ModelTemplates.FLAT_ITEM);
    }

    @Override
    public @NonNull String getName() {
        return "Model";
    }
}
