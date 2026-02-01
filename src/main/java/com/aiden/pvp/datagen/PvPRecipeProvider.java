package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItemTags;
import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PvPRecipeProvider extends FabricRecipeProvider {
    private final HolderGetter<Item> itemLookup;

    public PvPRecipeProvider(FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
        try {
            this.itemLookup = registriesFuture.get().lookupOrThrow(Registries.ITEM);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider wrapperLookup,
            @NotNull RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.MISC, ModItems.THROWABLE_DAGGER)
                        .define('S', Items.STICK)
                        .define('I', Items.IRON_INGOT)
                        .define('B', Items.IRON_BLOCK)
                        .pattern(" IB")
                        .pattern(" S ")
                        .pattern("S  ")
                        .unlockedBy("has_iron_ingot", this.has(Items.IRON_INGOT))
                        .group("throwable_dagger")
                        .save(this.output);
                shaped(RecipeCategory.MISC, ModItems.FIREBALL, 4)
                        .define('A', Items.BLAZE_POWDER)
                        .define('B', Items.GUNPOWDER)
                        .define('C', Items.NETHER_STAR)
                        .pattern("ABA")
                        .pattern("BCB")
                        .pattern("ABA")
                        .unlockedBy("has_nether_star", this.has(Items.NETHER_STAR))
                        .group("fireball")
                        .save(this.output);
                shaped(RecipeCategory.MISC, ModItems.GOLDEN_HEAD)
                        .define('A', Items.RESIN_BRICK)
                        .define('B', Items.GOLD_INGOT)
                        .define('C', Items.PLAYER_HEAD)
                        .pattern("ABA")
                        .pattern("BCB")
                        .pattern("ABA")
                        .unlockedBy("has_player_head", this.has(Items.PLAYER_HEAD))
                        .group("golden_head")
                        .save(this.output);
                shaped(RecipeCategory.MISC, ModItems.SELF_RES_PLATFORM, 4)
                        .define('A', Items.SLIME_BLOCK)
                        .define('B', Items.HEART_OF_THE_SEA)
                        .define('C', Items.BLAZE_ROD)
                        .pattern("ABA")
                        .pattern("BCB")
                        .pattern("ABA")
                        .unlockedBy("has_blaze_rod", this.has(Items.BLAZE_ROD))
                        .group("self-res_platform")
                        .save(this.output);
                shaped(RecipeCategory.MISC, Items.PLAYER_HEAD)
                        .define('A', Items.POISONOUS_POTATO)
                        .define('B', Items.ROTTEN_FLESH)
                        .define('C', Items.LINGERING_POTION)
                        .define('D', Items.SKELETON_SKULL)
                        .pattern("ABC")
                        .pattern("BDB")
                        .pattern("CBA")
                        .unlockedBy("has_skeleton_skull", this.has(Items.SKELETON_SKULL))
                        .group("player_head")
                        .save(this.output);
                offerBBUUpgradeRecipe(Items.WOODEN_SWORD, ModItems.WOODEN_SWORD);
                offerBBUUpgradeRecipe(Items.STONE_SWORD, ModItems.STONE_SWORD);
                offerBBUUpgradeRecipe(Items.IRON_SWORD, ModItems.IRON_SWORD);
                offerBBUUpgradeRecipe(Items.DIAMOND_SWORD, ModItems.DIAMOND_SWORD);
            }

            public void offerBBUUpgradeRecipe(Item input, Item result) {
                SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(input),
                        Ingredient.of(itemLookup.getOrThrow(ModItemTags.EMPTY)),
                        RecipeCategory.COMBAT,
                        result)
                        .unlocks("has_bbu_upgrade_template",
                                this.has(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE))
                        .save(this.output, "bbu_" + getItemName(result) + "_smithing");
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}