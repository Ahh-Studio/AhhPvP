package com.aiden.pvp;

import com.aiden.pvp.blocks.DefenseTowerBlock;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.entities.MurdererEntity;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.payloads.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
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
		ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(PvP.MOD_ID, "assets/pvp"));
		CreativeModeTabEvents.modifyOutputEvent(tabKey).register((FabricCreativeModeTabOutput output) -> {
			output.accept(new ItemStack(ModItems.FIREBALL));
			output.accept(new ItemStack(ModItems.SELF_RES_PLATFORM));
			output.accept(new ItemStack(ModItems.BRIDGE_EGG));
			output.accept(new ItemStack(ModItems.FISHING_ROD));
			output.accept(new ItemStack(ModItems.BED_BUG));
			output.accept(new ItemStack(ModItems.RETURN_SCROLL));
			output.accept(new ItemStack(ModItems.CHICKEN_DEFENSE));
			output.accept(new ItemStack(ModItems.TNT));
			output.accept(new ItemStack(ModItems.THROWABLE_TNT));
			output.accept(new ItemStack(ModItems.STRONG_GLASS));
			output.accept(new ItemStack(ModItems.WHITE_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.LIGHT_GRAY_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.GRAY_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.BLACK_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.BROWN_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.RED_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.ORANGE_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.YELLOW_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.LIME_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.GREEN_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.CYAN_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.LIGHT_BLUE_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.BLUE_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.PURPLE_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.MAGENTA_STRONG_GLASS));
			output.accept(new ItemStack(ModItems.PINK_STRONG_GLASS));
			// 未开发完成
			// output.accept(new ItemStack(ModItems.DEFENSE_TOWER));
			output.accept(new ItemStack(ModItems.BOSS_SPAWNER));
			output.accept(new ItemStack(ModItems.BOSS_KEY));
			output.accept(new ItemStack(ModItems.GOLDEN_HEAD));
			output.accept(new ItemStack(ModItems.CARBON_RUNE));
			output.accept(new ItemStack(ModItems.IRON_RUNE));
			output.accept(new ItemStack(ModItems.WOODEN_SWORD));
			output.accept(new ItemStack(ModItems.STONE_SWORD));
			output.accept(new ItemStack(ModItems.IRON_SWORD));
			output.accept(new ItemStack(ModItems.DIAMOND_SWORD));
			output.accept(new ItemStack(ModItems.THROWABLE_DAGGER));
			output.accept(new ItemStack(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE));
			output.accept(new ItemStack(ModItems.MURDERER_SPAWN_EGG));
			output.accept(new ItemStack(ModItems.LANDMINE));
			output.accept(new ItemStack(ModItems.AXE_OF_MURDER));
			BuiltInRegistries.POTION.listElements()
					.filter(potionReference -> potionReference.value().isEnabled(output.getContext().enabledFeatures()) && (potionReference.is(ModItems.LONG_INVISIBILITY_POTION) || potionReference.is(ModItems.SHORT_INVISIBILITY_POTION)))
					.map(potionReference -> PotionContents.createItemStack(Items.POTION, potionReference))
					.forEach(stack -> output.accept(stack));
		});
		ModBlockEntityTypes.initialize();
		ModEntityTypes.initialize();
		FabricDefaultAttributeRegistry.register(ModEntityTypes.MURDERER, MurdererEntity.createMurdererAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.CHICKEN_DEFENSE, com.aiden.pvp.entities.ChickenDefenseEntity.createAttributes());
		ModCommands.initialize();

		ServerTickEvents.END_SERVER_TICK.register(server ->
				server.levelKeys().forEach(key -> {
					var level = server.getLevel(key);
					if (level != null && DefenseTowerBlock.hasPendingTasks()) {
						DefenseTowerBlock.processTasks(level);
					}
				})
		);

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