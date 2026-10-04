package com.aiden.pvp.mixin;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.aiden.pvp.payloads.UpdateInfoToClientPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityPvpExtension {
    private @Unique FishingBobberEntity pvpFishHook = null;
    public @Unique int selfRescuePlatformCooldown = 0;
    public @Unique int returnScrollTeleportCountDown = 100;
    public @Unique boolean teleportingUsingReturnScroll = false;
    public @Unique int sendClientPacketCountdown = 0;

    @Override
    @Unique
    public void AhhPvP$setPvpFishHook(FishingBobberEntity fishingBobberEntity) {
        pvpFishHook = fishingBobberEntity;
    }

    @Override
    @Unique
    public FishingBobberEntity AhhPvP$getPvpFishHook() {
        return this.pvpFishHook;
    }

    @Override
    public void AhhPvP$setSelfRescuePlatformCooldown(int cooldown) {
        this.selfRescuePlatformCooldown = cooldown;
    }

    @Override
    public int AhhPvP$getSelfRescuePlatformCooldown() {
        return this.selfRescuePlatformCooldown;
    }

    @Override
    public void AhhPvP$setReturnScrollTeleportCountDown(int countDown) {
        this.returnScrollTeleportCountDown = countDown;
    }

    @Override
    public int AhhPvP$getReturnScrollTeleportCountDown() {
        return this.returnScrollTeleportCountDown;
    }

    @Override
    public void AhhPvP$setTeleportingUsingReturnScroll(boolean isTeleportingUsingReturnScroll) {
        this.teleportingUsingReturnScroll = isTeleportingUsingReturnScroll;
    }

    @Override
    public boolean AhhPvP$isTeleportingUsingReturnScroll() {
        return this.teleportingUsingReturnScroll;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(CallbackInfo ci) {
        if (this.pvpFishHook != null && !this.pvpFishHook.isRemoved()) {
            this.pvpFishHook.discard();
            this.pvpFishHook = null;
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            )
    )
    public void tick(CallbackInfo ci) {
        Player instance = (Player) (Object) this;

        // 发网络包向客户端同步数据
        if (this.sendClientPacketCountdown > 0) this.sendClientPacketCountdown--;
        else {
            if (instance instanceof ServerPlayer serverPlayer) {
                UpdateInfoToClientPayload payload = new UpdateInfoToClientPayload(this.teleportingUsingReturnScroll, this.returnScrollTeleportCountDown);
                PacketDistributor.sendToPlayer(serverPlayer, payload);
            }
            this.sendClientPacketCountdown = 10;
        }

        if (!instance.getMainHandItem().is(ModItems.FISHING_ROD) && !instance.getOffhandItem().is(ModItems.FISHING_ROD) && this.pvpFishHook != null) {
            this.pvpFishHook.discard();
            this.pvpFishHook = null;
        }
        if (this.selfRescuePlatformCooldown > 0) {
            this.selfRescuePlatformCooldown--;
        }
        if (instance.getKnownMovement().horizontalDistanceSqr() + (instance.getKnownMovement().y * instance.getKnownMovement().y) == 0
                && this.teleportingUsingReturnScroll
                && this.returnScrollTeleportCountDown > 0
        ) {
            this.returnScrollTeleportCountDown--;
        } else {
            this.returnScrollTeleportCountDown = 100;
            this.teleportingUsingReturnScroll = false;
        }
        if (this.teleportingUsingReturnScroll && returnScrollTeleportCountDown == 0) {
            if (instance.level() instanceof ServerLevel serverLevel && instance instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.getRespawnConfig() != null && (serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.black())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.blue())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.brown())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.cyan())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.gray())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.green())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.lightBlue())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.lime())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.lightGray())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.magenta())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.orange())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.pink())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.purple())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.red())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.white())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BED.yellow())
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.RESPAWN_ANCHOR))) {
                    Vec3 respawnCenter = Vec3.atCenterOf(serverPlayer.getRespawnConfig().respawnData().pos());
                    serverPlayer.teleportTo(
                            serverLevel,
                            respawnCenter.x(),
                            respawnCenter.y() + 0.5,
                            respawnCenter.z(),
                            Set.of(),
                            0.0F,
                            0.0F,
                            true
                    );
                }
            }
        }

    }

    @Inject(
            method = "isSecondaryUseActive",
            at = @At("HEAD"),
            cancellable = true
    )
    public void shouldCancelInteraction(CallbackInfoReturnable<Boolean> cir) {
        Player instance = (Player) (Object) this;
        cir.setReturnValue(instance.isShiftKeyDown() || instance.isBlocking());
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At(
                    value = "RETURN"
            )
    )
    public void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("SelfRescuePlatformCooldown", this.selfRescuePlatformCooldown);
        valueOutput.putInt("ReturnScrollTeleportCountDown", this.returnScrollTeleportCountDown);
        valueOutput.putBoolean("TeleportingUsingReturnScroll", this.teleportingUsingReturnScroll);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At(
                    value = "RETURN"
            )
    )
    public void readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
        this.selfRescuePlatformCooldown = valueInput.getIntOr("SelfRescuePlatformCooldown", 0);
        this.returnScrollTeleportCountDown = valueInput.getIntOr("ReturnScrollTeleportCountDown", 100);
        this.teleportingUsingReturnScroll = valueInput.getBooleanOr("TeleportingUsingReturnScroll", false);
    }
}