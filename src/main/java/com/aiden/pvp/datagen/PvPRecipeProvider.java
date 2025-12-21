package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PvPRecipeProvider extends FabricRecipeProvider {
    public PvPRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeGenerator getRecipeGenerator(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                createShaped(RecipeCategory.MISC, ModItems.THROWABLE_DAGGER)
                        .input('S', Items.STICK)
                        .input('I', Items.IRON_INGOT)
                        .input('B', Items.IRON_BLOCK)
                        .pattern(" IB")
                        .pattern(" S ")
                        .pattern("S  ")
                        .criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
                        .group("throwable_dagger")
                        .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
