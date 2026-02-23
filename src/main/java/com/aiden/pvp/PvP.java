package com.aiden.pvp;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.payloads.*;
import com.aiden.pvp.screen.SettingsScreen;

import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(PvP.MOD_ID)
public class PvP {
	public static final String MOD_ID = "pvp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public PvP(IEventBus modBus, ModContainer container) {
		ModGameRules.initialize();
		ModBlocks.initialize();
		ModItems.initialize();
		ModBlockEntityTypes.initialize();
		ModEntityTypes.initialize();
		ModCommands.initialize();

		Item fireballItem = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "fireball"));
		ProjectileDispenseBehavior projectileDispenserBehavior = new ProjectileDispenseBehavior(fireballItem);
		DispenserBlock.registerBehavior(fireballItem, projectileDispenserBehavior);

		// register the client=>server packet of setting game rules
		PayloadTypeRegistry.playC2S().register(SetGameRulesC2SPayload.ID, SetGameRulesC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SetGameRulesC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerLevel serverWorld = context.player().level();
				serverWorld.getGameRules().set(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER, payload.value1(), serverWorld.getServer());
				serverWorld.getGameRules().set(ModGameRules.PHDI, payload.value2(), serverWorld.getServer());
			});
		});

		// register the server=>client packet of getting game rules
		PayloadTypeRegistry.playS2C().register(GetGameRulesS2CPayload.ID, GetGameRulesS2CPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(GetGameRulesS2CPayload.ID, (payload, context) -> {
			if (context.client().screen instanceof SettingsScreen settingsScreen) {
				settingsScreen.setSliderValues(
						payload.value1(),
						payload.value2()
				);
			}
		});

		// register the client=>server packet of getting game rules (triggers the returning packet)
		PayloadTypeRegistry.playC2S().register(GetGameRulesC2SPayload.ID, GetGameRulesC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GetGameRulesC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				Entity entity = context.player().level().getEntity(payload.playerId());
				if (entity instanceof ServerPlayer serverPlayer) {
					ServerPlayNetworking.send(
							serverPlayer,
							new GetGameRulesS2CPayload(
									serverPlayer.level().getGameRules().get(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER),
									serverPlayer.level().getGameRules().get(ModGameRules.PHDI)
							)
					);
				}
			});
		});

		LOGGER.info("[Main] Mod Initialized Successfully! ");
	}
}
