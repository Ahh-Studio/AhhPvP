package com.aiden.pvp.payloads.handler;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.payloads.GetGameRulesC2SPayload;
import com.aiden.pvp.payloads.GetGameRulesS2CPayload;
import com.aiden.pvp.payloads.SetGameRulesC2SPayload;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ServerPayloadHandler {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(ThrowTntC2SPayload.TYPE, ThrowTntC2SPayload.CODEC, (throwTntC2SPayload, context) -> {
            context.enqueueWork(() -> {
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
        });

        registrar.playToServer(SetGameRulesC2SPayload.TYPE, SetGameRulesC2SPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                Level level = context.player().level();
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.getGameRules().set(ModGameRules.FIREBALL_EXPLODE_POWER.get(), payload.value1(), serverLevel.getServer());
                    serverLevel.getGameRules().set(ModGameRules.PHDI.get(), payload.value2(), serverLevel.getServer());
                }
            });
        });

        registrar.playToServer(GetGameRulesC2SPayload.TYPE,  GetGameRulesC2SPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                Entity entity = context.player().level().getEntity(payload.playerId());
                if (entity instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(
                            serverPlayer,
                            new GetGameRulesS2CPayload(
                                    serverPlayer.level().getGameRules().get(ModGameRules.FIREBALL_EXPLODE_POWER.get()),
                                    serverPlayer.level().getGameRules().get(ModGameRules.PHDI.get())
                            )
                    );
                }
            });
        });

        registrar.playToClient(GetGameRulesS2CPayload.TYPE, GetGameRulesS2CPayload.CODEC);
    }
}
