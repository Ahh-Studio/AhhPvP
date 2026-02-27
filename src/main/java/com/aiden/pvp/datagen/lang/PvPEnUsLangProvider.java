package com.aiden.pvp.datagen.lang;

import com.aiden.pvp.PvP;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class PvPEnUsLangProvider extends LanguageProvider {
    public PvPEnUsLangProvider(PackOutput output) {
        super(output, PvP.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("block.pvp.boss_spawner", "Boss Spawner");
        this.add("block.pvp.golden_head", "Golden Head");
        this.add("block.pvp.slime_block", "Special Slime Block");
        this.add("block.pvp.strong_glass", "Strong Glass");
        this.add("block.pvp.throwable_tnt", "Throwable TNT");
        this.add("block.pvp.tnt", "TNT");

        this.add("entity.pvp.bed_bug", "Bed Bug");
        this.add("entity.pvp.bridge_egg", "Bridge Egg");
        this.add("entity.pvp.dagger", "Dagger");
        this.add("entity.pvp.fireball", "Fireball");
        this.add("entity.pvp.fishing_bobber", "Fishing Bobber");
        this.add("entity.pvp.murderer", "Murderer");

        this.add("item.pvp.bbu_upgrade_smithing_template", "Before-battle-update Upgrade");
        this.add("item.pvp.bed_bug", "Bed Bug");
        this.add("item.pvp.boss_key", "Boss Key");
        this.add("item.pvp.bridge_egg", "Bridge Egg");
        this.add("item.pvp.carbon_rune", "Carbon Rune");
        this.add("item.pvp.chicken_defense", "Chicken Defense");
        this.add("item.pvp.diamond_sword", "Diamond Sword");
        this.add("item.pvp.fireball", "Fireball");
        this.add("item.pvp.fishing_rod", "Fishing Rod");
        this.add("item.pvp.iron_rune", "Iron Rune");
        this.add("item.pvp.iron_sword", "Iron Sword");
        this.add("item.pvp.murderer_spawn_egg", "Murderer Spawn Egg");
        this.add("item.pvp.return_scroll", "Return Scroll");
        this.add("item.pvp.self-res_platform", "Self-res Platform");
        this.add("item.pvp.stone_sword", "Stone Sword");
        this.add("item.pvp.throwable_dagger", "Throwable Dagger");
        this.add("item.pvp.wooden_sword", "Wooden Sword");
        this.add("item.pvp.smithing_template.bbu_upgrade.additions_slot_description", "Add Nothing");
        this.add("item.pvp.smithing_template.bbu_upgrade.applies_to", "Vanilla Swords");
        this.add("item.pvp.smithing_template.bbu_upgrade.base_slot_description", "Add Vanilla Swords");
        this.add("item.pvp.smithing_template.bbu_upgrade.ingredients", "Nothing");

        this.add("itemGroup.pvp_mod", "PvP Mod");

        this.add("screen.pvp.settings", "PvP Mod Settings");

        this.add("tag.item.pvp.empty", "Empty");
    }
}
