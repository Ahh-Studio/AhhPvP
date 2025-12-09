package com.aiden.pvp.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {
    public static GameRules.Key<GameRules.IntRule> PHDI;
    public static GameRules.Key<GameRules.BooleanRule> PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY; // 声明变量
    public static GameRules.Key<GameRules.IntRule> PvpMod_FIREBALL_SHOOT_POWER; // 声明变量
    public static GameRules.Key<GameRules.IntRule> PvpMod_FIREBALL_EXPLODE_POWER; // 声明变量
    public static void initialize() {
        // 赋值/注册
        PHDI = GameRuleRegistry.register(
                "phdi",
                GameRules.Category.MISC,
                GameRuleFactory.createIntRule(10, 0, 10)
        );
        PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY = GameRuleRegistry.register(
                "pvpModDoShooterVelocityAffectsFireballVelocity",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(false)
        );
        PvpMod_FIREBALL_SHOOT_POWER = GameRuleRegistry.register(
                "pvpModFireballShootPower",
                GameRules.Category.MISC,
                GameRuleFactory.createIntRule(12, 0, 2147483647)
        );
        PvpMod_FIREBALL_EXPLODE_POWER = GameRuleRegistry.register(
                "pvpModFireballExplodePower",
                GameRules.Category.MISC,
                GameRuleFactory.createIntRule(16, 0, 2147483647)
        );
    }
}
