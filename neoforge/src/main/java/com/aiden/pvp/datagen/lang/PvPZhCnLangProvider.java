package com.aiden.pvp.datagen.lang;

import com.aiden.pvp.PvP;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class PvPZhCnLangProvider extends LanguageProvider {
    public PvPZhCnLangProvider(PackOutput output) {
        super(output, PvP.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        this.add("block.pvp.boss_spawner", "Boss召唤器");
        this.add("block.pvp.defense_tower", "Defense Tower");
        this.add("block.pvp.golden_head", "金头");
        this.add("block.pvp.landmine", "地雷");
        this.add("block.pvp.slime_block", "特殊的黏液块");
        this.add("block.pvp.strong_glass", "钢化玻璃");
        this.add("block.pvp.black_strong_glass", "黑色钢化玻璃");
        this.add("block.pvp.blue_strong_glass", "蓝色钢化玻璃");
        this.add("block.pvp.brown_strong_glass", "棕色钢化玻璃");
        this.add("block.pvp.cyan_strong_glass", "青色钢化玻璃");
        this.add("block.pvp.gray_strong_glass", "灰色钢化玻璃");
        this.add("block.pvp.green_strong_glass", "绿色钢化玻璃");
        this.add("block.pvp.light_blue_strong_glass", "淡蓝色钢化玻璃");
        this.add("block.pvp.light_gray_strong_glass", "淡灰色钢化玻璃");
        this.add("block.pvp.lime_strong_glass", "黄绿色钢化玻璃");
        this.add("block.pvp.magenta_strong_glass", "品红色钢化玻璃");
        this.add("block.pvp.orange_strong_glass", "橙色钢化玻璃");
        this.add("block.pvp.pink_strong_glass", "粉色钢化玻璃");
        this.add("block.pvp.purple_strong_glass", "紫色钢化玻璃");
        this.add("block.pvp.red_strong_glass", "红色钢化玻璃");
        this.add("block.pvp.white_strong_glass", "白色钢化玻璃");
        this.add("block.pvp.yellow_strong_glass", "黄色钢化玻璃");
        this.add("block.pvp.throwable_tnt", "可投掷的TNT");
        this.add("block.pvp.tnt", "TNT");

        this.add("entity.pvp.bed_bug", "床豸");
        this.add("entity.pvp.bridge_egg", "搭桥蛋");
        this.add("entity.pvp.dagger", "飞刀");
        this.add("entity.pvp.fireball", "火球");
        this.add("entity.pvp.fishing_bobber", "浮标");
        this.add("entity.pvp.murderer", "杀手");

        this.add("item.pvp.axe_of_murder", "谋杀之斧");
        this.add("item.pvp.bbu_upgrade_smithing_template", "低版本升级");
        this.add("item.pvp.bed_bug", "床虱");
        this.add("item.pvp.boss_key", "Boss召唤器激活钥匙");
        this.add("item.pvp.bridge_egg", "搭桥蛋");
        this.add("item.pvp.carbon_rune", "碳化符文");
        this.add("item.pvp.chicken_defense", "小鸡炮台");
        this.add("item.pvp.diamond_sword", "钻石剑");
        this.add("item.pvp.fireball", "烈焰弹");
        this.add("item.pvp.fishing_rod", "钓鱼竿");
        this.add("item.pvp.iron_rune", "铁化符文");
        this.add("item.pvp.iron_sword", "铁剑");
        this.add("item.pvp.murderer_spawn_egg", "杀手刷怪蛋");
        this.add("item.pvp.return_scroll", "回城卷轴");
        this.add("item.pvp.self-res_platform", "救援平台");
        this.add("item.pvp.stone_sword", "石剑");
        this.add("item.pvp.throwable_dagger", "飞刀");
        this.add("item.pvp.wooden_sword", "木剑");
        this.add("item.pvp.smithing_template.bbu_upgrade.additions_slot_description", "无需加入任何物品");
        this.add("item.pvp.smithing_template.bbu_upgrade.applies_to", "原版剑");
        this.add("item.pvp.smithing_template.bbu_upgrade.base_slot_description", "加入原版剑");
        this.add("item.pvp.smithing_template.bbu_upgrade.ingredients", "无");

        this.add("itemGroup.pvp_mod", "PvP模组");

        this.add("key.category.pvp.pvp", "PvP模组");
        this.add("key.pvp.open_settings", "打开设置");
        this.add("key.pvp.throw_tnt", "投掷TNT");

        this.add("screen.pvp.settings", "PvP模组设置");

        this.add("tag.item.pvp.empty", "空");
    }
}