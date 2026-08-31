package com.aiden.pvp.datagen;

import com.aiden.pvp.PvP;
import com.aiden.pvp.items.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class PvPModelProvider extends ModelProvider {
    public PvPModelProvider(PackOutput output) {
        super(output, PvP.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.RETURN_SCROLL, ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.EGGLLET, ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHICKEN_DEFENSE, ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.AXE_OF_MURDER, ModelTemplates.FLAT_ITEM);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.of(
                Holder.direct(ModItems.RETURN_SCROLL),
                Holder.direct(ModItems.EGGLLET),
                Holder.direct(ModItems.CHICKEN_DEFENSE),
                Holder.direct(ModItems.AXE_OF_MURDER)
        );
    }
}