package com.aiden.pvp.gamerules;

import com.aiden.pvp.PvP;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.*;

import java.util.function.ToIntFunction;

public class ModGameRules {
    public static GameRule<Integer> PHDI;
    public static GameRule<Boolean> PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY;
    public static GameRule<Integer> PvpMod_FIREBALL_SHOOT_POWER;
    public static GameRule<Integer> PvpMod_FIREBALL_EXPLODE_POWER;
    public static GameRule<Boolean> PvpMod_FIREBALL_CREATES_FIRE;
    public static void initialize() {
        try {
            PHDI = registerIntRule(
                    "phdi",
                    GameRuleCategory.MISC, 10, 0, 10
            );
            PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY = registerBooleanRule(
                    "pvp_mod_do_shooter_velocity_affects_fireball_velocity",
                    GameRuleCategory.MISC, false
            );
            PvpMod_FIREBALL_SHOOT_POWER = registerIntRule(
                    "pvp_mod_fireball_shoot_power",
                    GameRuleCategory.MISC, 12, 0
            );
            PvpMod_FIREBALL_EXPLODE_POWER = registerIntRule(
                    "pvp_mod_fireball_explode_power", 
                    GameRuleCategory.MISC, 16, 0
            );
            PvpMod_FIREBALL_CREATES_FIRE = registerBooleanRule(
                    "fireball_creates_fire",
                    GameRuleCategory.MISC, true
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
                FeatureFlagSet.of(),
                GameRuleTypeVisitor::visitBoolean,
                value -> value ? 1 : 0
        );
    }

    private static GameRule<Integer> registerIntRule(String name, GameRuleCategory category, int defaultValue, int minValue) {
        return registerIntRule(name, category, defaultValue, minValue, Integer.MAX_VALUE, FeatureFlagSet.of());
    }

    private static GameRule<Integer> registerIntRule(String name, GameRuleCategory category, int defaultValue, int minValue, int maxValue) {
        return registerIntRule(name, category, defaultValue, minValue, maxValue, FeatureFlagSet.of());
    }

    private static GameRule<Integer> registerIntRule(
            String name, GameRuleCategory category, int defaultValue, int minValue, int maxValue, FeatureFlagSet requiredFeatures
    ) {
        return register(
                name,
                category,
                GameRuleType.INT,
                IntegerArgumentType.integer(minValue, maxValue),
                Codec.intRange(minValue, maxValue),
                defaultValue,
                requiredFeatures,
                GameRuleTypeVisitor::visitInteger,
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
            FeatureFlagSet requiredFeatures,
            GameRules.VisitorCaller<T> acceptor,
            ToIntFunction<T> commandResultSupplier
    ) {
        return Registry.register(
                BuiltInRegistries.GAME_RULE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, name), new GameRule<>(category, type, argumentType, acceptor, codec, commandResultSupplier, defaultValue, requiredFeatures)
        );
    }
}
