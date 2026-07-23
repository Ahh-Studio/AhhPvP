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
    public static GameRule<Boolean> SHOOTER_AFFECTS_FIREBALL_VELOCITY;
    public static GameRule<Integer> FIREBALL_SHOOT_POWER;
    public static GameRule<Integer> FIREBALL_EXPLODE_POWER;
    public static GameRule<Boolean> FIREBALL_CREATES_FIRE;
    public static GameRule<Integer> SELF_RES_PLATFORM_CD;
    public static GameRule<Integer> SELF_RES_PLATFORM_DISAPPEAR_TIME;
    public static void initialize() {
        try {
            PHDI = registerIntRule("phdi", 10, 0, 10);
            FIREBALL_SHOOT_POWER = registerIntRule("fireball_shoot_power", 12, 0);
            FIREBALL_EXPLODE_POWER = registerIntRule("fireball_explode_power", 16, 0);
            FIREBALL_CREATES_FIRE = registerBoolRule("fireball_creates_fire", true);
            SHOOTER_AFFECTS_FIREBALL_VELOCITY = registerBoolRule("shooter_affects_fireball_velocity", false);
            SELF_RES_PLATFORM_CD = registerIntRule("self-res_platform_cd", 400, -1, 1600);
            SELF_RES_PLATFORM_DISAPPEAR_TIME = registerIntRule("self-res_platform_disappear_time", 400, -1, 1600);

            PvP.LOGGER.info("[Game Rules Initializer] Mod Game Rules Initialized! ");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Game Rules Initializer] An Error Occurred! ");
        }
    }

    private static GameRule<Boolean> registerBoolRule(String name, boolean defaultValue) {
        return register(
                name,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                Codec.BOOL,
                defaultValue,
                FeatureFlagSet.of(),
                GameRuleTypeVisitor::visitBoolean,
                value -> value ? 1 : 0
        );
    }

    private static GameRule<Integer> registerIntRule(String name, int defaultValue, int minValue) {
        return registerIntRule(name, defaultValue, minValue, Integer.MAX_VALUE, FeatureFlagSet.of());
    }

    private static GameRule<Integer> registerIntRule(String name, int defaultValue, int minValue, int maxValue) {
        return registerIntRule(name, defaultValue, minValue, maxValue, FeatureFlagSet.of());
    }

    private static GameRule<Integer> registerIntRule(
            String name, int defaultValue, int minValue, int maxValue, FeatureFlagSet requiredFeatures
    ) {
        return register(
                name,
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
            GameRuleType type,
            ArgumentType<T> argumentType,
            Codec<T> codec,
            T defaultValue,
            FeatureFlagSet requiredFeatures,
            GameRules.VisitorCaller<T> acceptor,
            ToIntFunction<T> commandResultSupplier
    ) {
        return Registry.register(
                BuiltInRegistries.GAME_RULE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, name), new GameRule<>(GameRuleCategory.MISC, type, argumentType, acceptor, codec, commandResultSupplier, defaultValue, requiredFeatures)
        );
    }
}
