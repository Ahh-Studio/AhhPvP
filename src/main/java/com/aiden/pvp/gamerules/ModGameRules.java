package com.aiden.pvp.gamerules;

import com.aiden.pvp.PvP;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModGameRules {
    public static final DeferredRegister<GameRule<?>> GAME_RULES = DeferredRegister.create(Registries.GAME_RULE, PvP.MOD_ID);

    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> PHDI = GAME_RULES.register(
            "phdi",
            identifier -> registerIntRule(GameRuleCategory.MISC, 10, 0, 10)
    );
    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY = GAME_RULES.register(
            "shooter_velocity_affects_fireball_velocity",
            identifier -> registerBooleanRule(GameRuleCategory.MISC, false)
    );
    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> FIREBALL_SHOOT_POWER = GAME_RULES.register(
            "fireball_shoot_power",
            identifier -> registerIntRule(GameRuleCategory.MISC, 12, 0)
    );
    public static final DeferredHolder<GameRule<?>, GameRule<Integer>> FIREBALL_EXPLODE_POWER = GAME_RULES.register(
            "fireball_explode_power",
            identifier -> registerIntRule(GameRuleCategory.MISC, 16, 0)
    );

    public static void initialize(IEventBus modBus) {
        try {
            GAME_RULES.register(modBus);
            PvP.LOGGER.info("[Game Rules Initializer] Mod Game Rules Initialized! ");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Game Rules Initializer] An Error Occurred: \n{}", e.getMessage());
        }
    }

    private static GameRule<Boolean> registerBooleanRule(GameRuleCategory category, boolean defaultValue) {
        return new GameRule<>(
                category, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean, Codec.BOOL,
                value -> value ? 1 : 0, defaultValue, FeatureFlagSet.of()
        );
    }

    private static GameRule<Integer> registerIntRule(GameRuleCategory category, int defaultValue, int minValue) {
        return registerIntRule(category, defaultValue, minValue, Integer.MAX_VALUE);
    }

    private static GameRule<Integer> registerIntRule(GameRuleCategory category, int defaultValue, int minValue, int maxValue) {
        return new GameRule<>(
                category, GameRuleType.INT, IntegerArgumentType.integer(minValue, maxValue), GameRuleTypeVisitor::visitInteger, Codec.intRange(minValue, maxValue),
                value -> value, defaultValue, FeatureFlagSet.of()
        );
    }
}
