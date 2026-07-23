package com.aiden.pvp.payloads;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;

public class PacketListeners {
    public static void throwTntC2SPayloadListener(ThrowTntC2SPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            Entity user = context.player().level().getEntity(payload.userId());
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
    }

    public static void setGameRulesC2SPayloadListener(SetGameRulesC2SPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (!context.player().permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) return;
            ServerLevel serverWorld = context.player().level();
            serverWorld.getGameRules().set(ModGameRules.FIREBALL_EXPLODE_POWER, payload.value1(), serverWorld.getServer());
            serverWorld.getGameRules().set(ModGameRules.PHDI, payload.value2(), serverWorld.getServer());
            serverWorld.getGameRules().set(ModGameRules.FIREBALL_CREATES_FIRE, payload.value3(), serverWorld.getServer());
        });
    }

    public static void getGameRulesS2CPayloadListener(GetGameRulesS2CPayload payload, ClientPlayNetworking.Context context) {
        if (context.client().screen instanceof SettingsScreen settingsScreen) {
            settingsScreen.setSliderValues(
                    payload.value1(),
                    payload.value2(),
                    payload.value3()
            );
        }
    }

    public static void getGameRulesC2SPayloadListener(GetGameRulesC2SPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            Entity entity = context.player().level().getEntity(payload.playerId());
            if (entity instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(
                        serverPlayer,
                        new GetGameRulesS2CPayload(
                                serverPlayer.level().getGameRules().get(ModGameRules.FIREBALL_EXPLODE_POWER),
                                serverPlayer.level().getGameRules().get(ModGameRules.PHDI),
                                serverPlayer.level().getGameRules().get(ModGameRules.FIREBALL_CREATES_FIRE)
                        )
                );
            }
        });
    }

    public static void updateInfoToClientPayload(UpdateInfoToClientPayload payload, ClientPlayNetworking.Context context) {
        LocalPlayer localPlayer = context.player();
        PlayerEntityPvpExtension playerExt = (PlayerEntityPvpExtension) localPlayer;
        playerExt.AhhPvP$setTeleportingUsingReturnScroll(payload.teleportingUsingReturnScroll());
        playerExt.AhhPvP$setReturnScrollTeleportCountDown(payload.returnScrollTeleportCountDown());
    }
}
