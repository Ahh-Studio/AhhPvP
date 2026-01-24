package com.aiden.pvp.gamerules;

import com.aiden.pvp.PvP;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.*;

import java.util.function.ToIntFunction;

public class ModGameRules {
    public static GameRule<Integer> PHDI;
    public static GameRule<Boolean> PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY; // 声明变量
    public static GameRule<Integer> PvpMod_FIREBALL_SHOOT_POWER; // 声明变量
    public static GameRule<Integer> PvpMod_FIREBALL_EXPLODE_POWER; // 声明变量
    public static void initialize() {
        try {
            PHDI = registerIntRule(
                    "phdi",
                    GameRuleCategory.MISC,
                    10, 0, 10
            );
            PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY = registerBooleanRule(
                    "pvp_mod_do_shooter_velocity_affects_fireball_velocity",
                    GameRuleCategory.MISC,
                    false
            );
            PvpMod_FIREBALL_SHOOT_POWER = registerIntRule(
                    "pvp_mod_fireball_shoot_power",
                    GameRuleCategory.MISC,
                    12, 0
            );
            PvpMod_FIREBALL_EXPLODE_POWER = registerIntRule(
                    "pvp_mod_fireball_explode_power",
                    GameRuleCategory.MISC,
                    16, 0
            );
            PvP.LOGGER.info("[Game Rules Initializer] Mod Game Rules Initialized! ");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Game Rules Initializer] An Error Occurred! ");
        }
    }

    private static GameRule<Boolean> registerBooleanRule(String name, GameRuleCategory category, boolean defaultValue) {
        return register(
                name,
                category,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                Codec.BOOL,
                defaultValue,
                FeatureSet.empty(),
                GameRuleVisitor::visitBoolean,
                value -> value ? 1 : 0
        );
    }

    private static GameRule<Integer> registerIntRule(String name, GameRuleCategory category, int defaultValue, int minValue) {
        return registerIntRule(name, category, defaultValue, minValue, Integer.MAX_VALUE, FeatureSet.empty());
    }

    private static GameRule<Integer> registerIntRule(String name, GameRuleCategory category, int defaultValue, int minValue, int maxValue) {
        return registerIntRule(name, category, defaultValue, minValue, maxValue, FeatureSet.empty());
    }

    private static GameRule<Integer> registerIntRule(
            String name, GameRuleCategory category, int defaultValue, int minValue, int maxValue, FeatureSet requiredFeatures
    ) {
        return register(
                name,
                category,
                GameRuleType.INT,
                IntegerArgumentType.integer(minValue, maxValue),
                Codec.intRange(minValue, maxValue),
                defaultValue,
                requiredFeatures,
                GameRuleVisitor::visitInt,
                value -> value
        );
    }

    private static <T> GameRule<T> register(
            String name,
            GameRuleCategory category,
            GameRuleType type,
            ArgumentType<T> argumentType,
            Codec<T> codec,
            T defaultValue,
            FeatureSet requiredFeatures,
            GameRules.Acceptor<T> acceptor,
            ToIntFunction<T> commandResultSupplier
    ) {
        return Registry.register(
                Registries.GAME_RULE, Identifier.of(PvP.MOD_ID, name), new GameRule<>(category, type, argumentType, acceptor, codec, commandResultSupplier, defaultValue, requiredFeatures)
        );
    }
}
