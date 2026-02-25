package com.aiden.pvp;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.client.PvPClient;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.datagen.PvPDataGenerator;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;

import com.aiden.pvp.payloads.handler.ClientPayloadHandler;
import com.aiden.pvp.payloads.handler.ServerPayloadHandler;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(PvP.MOD_ID)
public class PvP {
	public static final String MOD_ID = "pvp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public PvP(IEventBus modBus, ModContainer container) {
		modBus.addListener(ServerPayloadHandler::register);
		modBus.addListener(ClientPayloadHandler::register);
		NeoForge.EVENT_BUS.addListener(ModItems::handleRegisterBrewingRecipesEvent);
		NeoForge.EVENT_BUS.addListener(ModItems::handleItemToolTipEvent);
		modBus.addListener(ModEntityTypes::createAttributes);
		NeoForge.EVENT_BUS.addListener(PvPClient::onClientTick);
		modBus.addListener(PvPClient::registerKeyMappings);
		modBus.addListener(PvPDataGenerator::gatherData);
		modBus.addListener(PvP::commonSetupEventListener);
		NeoForge.EVENT_BUS.addListener(ModCommands::onRegisterClientCommands);

		ModGameRules.initialize(modBus);
		ModBlocks.initialize(modBus);
		ModItems.initialize(modBus);
		ModBlockEntityTypes.initialize(modBus);
		ModEntityTypes.initialize(modBus);

		LOGGER.info("[Main] Mod Initialized Successfully! ");
	}

	@SubscribeEvent
	public static void commonSetupEventListener(FMLCommonSetupEvent event) {
		Item fireballItem = ModItems.FIREBALL.get();
		ProjectileDispenseBehavior projectileDispenserBehavior = new ProjectileDispenseBehavior(fireballItem);
		DispenserBlock.registerBehavior(fireballItem, projectileDispenserBehavior);
	}
}
