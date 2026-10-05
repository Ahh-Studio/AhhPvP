package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItemTags;
import com.aiden.pvp.items.ModItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class PvPRecipeProvider extends RecipeProvider {
    public PvPRecipeProvider(BootstrapContext<Recipe<?>> recipes, BootstrapContext<net.minecraft.advancements.Advancement> advancements) {
        super(recipes, advancements);
    }

    @Override
    protected void buildRecipes() {
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
                .group("self_res_platform")
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
    }
}