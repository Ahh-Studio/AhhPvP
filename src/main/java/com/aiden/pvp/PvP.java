package com.aiden.pvp;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.commands.ModCommands;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.aiden.pvp.payloads.*;
import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

        // register the client=>server packet of throwing TNT
		PayloadTypeRegistry.playC2S().register(ThrowTntC2SPayload.ID, ThrowTntC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ThrowTntC2SPayload.ID, ((throwTntC2SPayload, context) -> {
			context.server().execute(() -> {
				Entity user = context.player().level().getEntity(throwTntC2SPayload.userId());
                if (user instanceof LivingEntity livingEntityUser) {
					PrimedTnt tnt = new PrimedTnt(user.level(), user.getX(), user.getEyeY(), user.getZ(), livingEntityUser);
					// Shoot
					Vec3 vec3d = new Vec3(
							-Mth.sin(user.getYRot() * (float) (Math.PI / 180.0)) * Mth.cos(user.getXRot() * (float) (Math.PI / 180.0)),
							-Mth.sin((user.getXRot() + 0.0F) * (float) (Math.PI / 180.0)),
							Mth.cos(user.getYRot() * (float) (Math.PI / 180.0)) * Mth.cos(user.getXRot() * (float) (Math.PI / 180.0))
					).normalize().add(RandomSource.create().triangle(0.0, 0.0172275 * 1.0F), RandomSource.create().triangle(0.0, 0.0172275 * 1.0F), RandomSource.create().triangle(0.0, 0.0172275 * 1.0F)).scale(1.5F);

					tnt.setDeltaMovement(vec3d);
					tnt.needsSync = true;
					double d = vec3d.horizontalDistance();
					tnt.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
					tnt.setXRot((float)(Mth.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
					tnt.yRotO = tnt.getYRot();
					tnt.xRotO = tnt.getXRot();
					tnt.setDeltaMovement(tnt.getDeltaMovement().add(user.getKnownMovement().x, user.onGround() ? 0.0 : user.getKnownMovement().y, user.getKnownMovement().z));
					if (livingEntityUser.getMainHandItem().is(ModItems.THROWABLE_TNT)) {
						user.level().addFreshEntity(tnt);
						livingEntityUser.getMainHandItem().consume(1, livingEntityUser);
						return;
					}
					if (livingEntityUser.getOffhandItem().is(ModItems.THROWABLE_TNT)) {
						user.level().addFreshEntity(tnt);
						livingEntityUser.getOffhandItem().consume(1, livingEntityUser);
					}
                }
            });
		}));

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
