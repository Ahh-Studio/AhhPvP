package com.aiden.pvp.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class PvPZhCnLangProvider extends FabricLanguageProvider {
    public PvPZhCnLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder builder) {
        builder.add("block.pvp.boss_spawner", "Boss召唤器");
        builder.add("block.pvp.golden_head", "金头");
        builder.add("block.pvp.slime_block", "特殊的黏液块");
        builder.add("block.pvp.strong_glass", "钢化玻璃");
        builder.add("block.pvp.throwable_tnt", "可投掷的TNT");
        builder.add("block.pvp.tnt", "TNT");

        builder.add("entity.pvp.bed_bug", "床豸");
        builder.add("entity.pvp.bridge_egg", "搭桥蛋");
        builder.add("entity.pvp.dagger", "飞刀");
        builder.add("entity.pvp.fireball", "火球");
        builder.add("entity.pvp.fishing_bobber", "浮标");
        builder.add("entity.pvp.murderer", "杀手");

        builder.add("item.pvp.bbu_upgrade_smithing_template", "低版本升级");
        builder.add("item.pvp.bed_bug", "床虱");
        builder.add("item.pvp.boss_key", "Boss召唤器激活钥匙");
        builder.add("item.pvp.bridge_egg", "搭桥蛋");
        builder.add("item.pvp.carbon_rune", "碳化符文");
        builder.add("item.pvp.diamond_sword", "钻石剑");
        builder.add("item.pvp.fireball", "烈焰弹");
        builder.add("item.pvp.fishing_rod", "钓鱼竿");
        builder.add("item.pvp.iron_rune", "铁化符文");
        builder.add("item.pvp.iron_sword", "铁剑");
        builder.add("item.pvp.murderer_spawn_egg", "杀手刷怪蛋");
        builder.add("item.pvp.return_scroll", "回城卷轴");
        builder.add("item.pvp.self-res_platform", "救援平台");
        builder.add("item.pvp.stone_sword", "石剑");
        builder.add("item.pvp.throwable_dagger", "飞刀");
        builder.add("item.pvp.wooden_sword", "木剑");
        builder.add("item.pvp.smithing_template.bbu_upgrade.additions_slot_description", "无需加入任何物品");
        builder.add("item.pvp.smithing_template.bbu_upgrade.applies_to", "原版剑");
        builder.add("item.pvp.smithing_template.bbu_upgrade.base_slot_description", "加入原版剑");
        builder.add("item.pvp.smithing_template.bbu_upgrade.ingredients", "无");

        builder.add("itemGroup.pvp_mod", "PvP模组");

        builder.add("screen.pvp.settings", "PvP模组设置");

        builder.add("tag.item.pvp.empty", "空");
    }
}
