package com.aiden.pvp.datagen.lang;

import com.aiden.pvp.PvP;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class PvPZhTwLangProvider extends LanguageProvider {
    public PvPZhTwLangProvider(PackOutput output) {
        super(output, PvP.MOD_ID, "zh_tw");
    }

    @Override
    protected void addTranslations() {
        this.add("block.pvp.boss_spawner", "頭目生成磚");
        this.add("block.pvp.golden_head", "金頭顱");
        this.add("block.pvp.slime_block", "特殊的史萊姆塊");
        this.add("block.pvp.strong_glass", "鋼化玻璃");
        this.add("block.pvp.throwable_tnt", "可投射的TNT");
        this.add("block.pvp.tnt", "TNT");

        this.add("entity.pvp.bed_bug", "床豸");
        this.add("entity.pvp.bridge_egg", "雞蛋橋");
        this.add("entity.pvp.dagger", "飛刀");
        this.add("entity.pvp.fireball", "火球");
        this.add("entity.pvp.fishing_bobber", "浮標");
        this.add("entity.pvp.murderer", "殺手");

        this.add("item.pvp.bbu_upgrade_smithing_template", "低版本升級");
        this.add("item.pvp.bed_bug", "蠹魚雪球");
        this.add("item.pvp.boss_key", "頭目生成之匙");
        this.add("item.pvp.bridge_egg", "雞蛋橋");
        this.add("item.pvp.carbon_rune", "碳之符文");
        this.add("item.pvp.diamond_sword", "鑽石劍");
        this.add("item.pvp.fireball", "火球");
        this.add("item.pvp.fishing_rod", "釣竿");
        this.add("item.pvp.iron_rune", "鐵之符文");
        this.add("item.pvp.iron_sword", "鐵劍");
        this.add("item.pvp.murderer_spawn_egg", "殺手生怪蛋");
        this.add("item.pvp.self-res_platform", "自救檯");
        this.add("item.pvp.stone_sword", "石劍");
        this.add("item.pvp.throwable_dagger", "飛刀");
        this.add("item.pvp.wooden_sword", "木劍");

        this.add("item.pvp.smithing_template.bbu_upgrade.additions_slot_description", "無需原料！");
        this.add("item.pvp.smithing_template.bbu_upgrade.applies_to", "原版劍");
        this.add("item.pvp.smithing_template.bbu_upgrade.base_slot_description", "添加原版劍");
        this.add("item.pvp.smithing_template.bbu_upgrade.ingredients", "無");

        this.add("itemGroup.pvp_mod", "PvP Mod");

        this.add("screen.pvp.settings", "PvP Mod設定");

        this.add("tag.item.pvp.empty", "空");
    }
}
