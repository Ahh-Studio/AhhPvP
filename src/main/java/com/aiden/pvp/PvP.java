package com.aiden.pvp;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.payloads.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvP implements ModInitializer {
	public static final String MOD_ID = "pvp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModGameRules.initialize();
		ModBlocks.initialize();
		ModItems.initialize();
		ModBlockEntityTypes.initialize();
		ModEntityTypes.initialize();
		ModCommands.initialize();

        Item fireballItem = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, "fireball"));
        ProjectileDispenseBehavior projectileDispenserBehavior = new ProjectileDispenseBehavior(fireballItem);
        DispenserBlock.registerBehavior(fireballItem, projectileDispenserBehavior);

        LOGGER.info("[Main] Registering Packets...");

        // 注册网络包
		PayloadTypeRegistry.serverboundPlay().register(ThrowTntC2SPayload.ID, ThrowTntC2SPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetGameRulesC2SPayload.ID, SetGameRulesC2SPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(GetGameRulesC2SPayload.ID, GetGameRulesC2SPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(GetGameRulesS2CPayload.ID, GetGameRulesS2CPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(UpdateInfoToClientPayload.ID, UpdateInfoToClientPayload.CODEC);

        // 注册网络包监听器
		ServerPlayNetworking.registerGlobalReceiver(ThrowTntC2SPayload.ID, PacketListeners::throwTntC2SPayloadListener);
        ServerPlayNetworking.registerGlobalReceiver(SetGameRulesC2SPayload.ID, PacketListeners::setGameRulesC2SPayloadListener);
        ServerPlayNetworking.registerGlobalReceiver(GetGameRulesC2SPayload.ID, PacketListeners::getGameRulesC2SPayloadListener);
        ClientPlayNetworking.registerGlobalReceiver(GetGameRulesS2CPayload.ID, PacketListeners::getGameRulesS2CPayloadListener);
		ClientPlayNetworking.registerGlobalReceiver(UpdateInfoToClientPayload.ID, PacketListeners::updateInfoToClientPayload);

		LOGGER.info("[Main] Mod Initialized Successfully! ");
	}
}
