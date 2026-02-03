package com.aiden.pvp.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class PvPEnUsLangProvider extends FabricLanguageProvider {
    public PvPEnUsLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, TranslationBuilder builder) {
        builder.add("block.pvp.boss_spawner", "Boss Spawner");
        builder.add("block.pvp.golden_head", "Golden Head");
        builder.add("block.pvp.slime_block", "Special Slime Block");
        builder.add("block.pvp.strong_glass", "Strong Glass");
        builder.add("block.pvp.throwable_tnt", "Throwable TNT");
        builder.add("block.pvp.tnt", "TNT");

        builder.add("entity.pvp.bed_bug", "Bed Bug");
        builder.add("entity.pvp.bridge_egg", "Bridge Egg");
        builder.add("entity.pvp.dagger", "Dagger");
        builder.add("entity.pvp.fireball", "Fireball");
        builder.add("entity.pvp.fishing_bobber", "Fishing Bobber");
        builder.add("entity.pvp.murderer", "Murderer");

        builder.add("item.pvp.bbu_upgrade_smithing_template", "Before-battle-update Upgrade");
        builder.add("item.pvp.bed_bug", "Bed Bug");
        builder.add("item.pvp.boss_key", "Boss Key");
        builder.add("item.pvp.bridge_egg", "Bridge Egg");
        builder.add("item.pvp.carbon_rune", "Carbon Rune");
        builder.add("item.pvp.diamond_sword", "Diamond Sword");
        builder.add("item.pvp.fireball", "Fireball");
        builder.add("item.pvp.fishing_rod", "Fishing Rod");
        builder.add("item.pvp.iron_rune", "Iron Rune");
        builder.add("item.pvp.iron_sword", "Iron Sword");
        builder.add("item.pvp.murderer_spawn_egg", "Murderer Spawn Egg");
        builder.add("item.pvp.return_scroll", "Return Scroll");
        builder.add("item.pvp.self-res_platform", "Self-res Platform");
        builder.add("item.pvp.stone_sword", "Stone Sword");
        builder.add("item.pvp.throwable_dagger", "Throwable Dagger");
        builder.add("item.pvp.wooden_sword", "Wooden Sword");
        builder.add("item.pvp.smithing_template.bbu_upgrade.additions_slot_description", "Add Nothing");
        builder.add("item.pvp.smithing_template.bbu_upgrade.applies_to", "Vanilla Swords");
        builder.add("item.pvp.smithing_template.bbu_upgrade.base_slot_description", "Add Vanilla Swords");
        builder.add("item.pvp.smithing_template.bbu_upgrade.ingredients", "Nothing");

        builder.add("itemGroup.pvp_mod", "PvP Mod");

        builder.add("screen.pvp.settings", "PvP Mod Settings");

        builder.add("tag.item.pvp.empty", "Empty");
    }
}
