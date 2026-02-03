package com.aiden.pvp.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class PvPZhTwLangProvider extends FabricLanguageProvider {
    public PvPZhTwLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "zh_tw", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        builder.add("block.pvp.boss_spawner", "頭目生成磚");
        builder.add("block.pvp.golden_head", "金頭顱");
        builder.add("block.pvp.slime_block", "特殊的史萊姆塊");
        builder.add("block.pvp.strong_glass", "鋼化玻璃");
        builder.add("block.pvp.throwable_tnt", "可投射的TNT");
        builder.add("block.pvp.tnt", "TNT");

        builder.add("entity.pvp.bed_bug", "床豸");
        builder.add("entity.pvp.bridge_egg", "雞蛋橋");
        builder.add("entity.pvp.dagger", "飛刀");
        builder.add("entity.pvp.fireball", "火球");
        builder.add("entity.pvp.fishing_bobber", "浮標");
        builder.add("entity.pvp.murderer", "殺手");

        builder.add("item.pvp.bbu_upgrade_smithing_template", "低版本升級");
        builder.add("item.pvp.bed_bug", "蠹魚雪球");
        builder.add("item.pvp.boss_key", "頭目生成之匙");
        builder.add("item.pvp.bridge_egg", "雞蛋橋");
        builder.add("item.pvp.carbon_rune", "碳之符文");
        builder.add("item.pvp.diamond_sword", "鑽石劍");
        builder.add("item.pvp.fireball", "火球");
        builder.add("item.pvp.fishing_rod", "釣竿");
        builder.add("item.pvp.iron_rune", "鐵之符文");
        builder.add("item.pvp.iron_sword", "鐵劍");
        builder.add("item.pvp.murderer_spawn_egg", "殺手生怪蛋");
        builder.add("item.pvp.self-res_platform", "自救檯");
        builder.add("item.pvp.stone_sword", "石劍");
        builder.add("item.pvp.throwable_dagger", "飛刀");
        builder.add("item.pvp.wooden_sword", "木劍");

        builder.add("item.pvp.smithing_template.bbu_upgrade.additions_slot_description", "無需原料！");
        builder.add("item.pvp.smithing_template.bbu_upgrade.applies_to", "原版劍");
        builder.add("item.pvp.smithing_template.bbu_upgrade.base_slot_description", "添加原版劍");
        builder.add("item.pvp.smithing_template.bbu_upgrade.ingredients", "無");

        builder.add("itemGroup.pvp_mod", "PvP Mod");

        builder.add("screen.pvp.settings", "PvP Mod設定");

        builder.add("tag.item.pvp.empty", "空");
    }
}
