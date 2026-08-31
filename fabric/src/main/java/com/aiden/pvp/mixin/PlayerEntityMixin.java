package com.aiden.pvp.mixin;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.aiden.pvp.payloads.UpdateInfoToClientPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityPvpExtension {
    private @Unique FishingBobberEntity pvpFishHook = null;
    public @Unique int selfRescuePlatformCooldown = 0;
    public @Unique int returnScrollTeleportCountDown = 100;
    public @Unique boolean teleportingUsingReturnScroll = false;
    public @Unique int sendClientPacketCountdown = 0;

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(CallbackInfo ci) {
        if (this.pvpFishHook != null && !this.pvpFishHook.isRemoved()) {
            this.pvpFishHook.discard();
            this.pvpFishHook = null;
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        Player instance = (Player) (Object) this;

        // 发网络包向客户端同步数据
        if (this.sendClientPacketCountdown > 0) this.sendClientPacketCountdown--;
        else {
            if (instance instanceof ServerPlayer serverPlayer) {
                UpdateInfoToClientPayload payload = new UpdateInfoToClientPayload(this.teleportingUsingReturnScroll, this.returnScrollTeleportCountDown);
                ServerPlayNetworking.send(serverPlayer, payload);
            }
            this.sendClientPacketCountdown = 10;
        }

        // 救援平台冷却时间减少
        if (this.selfRescuePlatformCooldown > 0) {
            this.selfRescuePlatformCooldown--;
        }

        // 手持非鱼竿就删除鱼钩
        if (!instance.getMainHandItem().is(ModItems.FISHING_ROD) && !instance.getOffhandItem().is(ModItems.FISHING_ROD) && this.pvpFishHook != null) {
            this.pvpFishHook.discard();
            this.pvpFishHook = null;
        }

        // 回城卷轴的移动取消判定、计时逻辑
        if (instance instanceof ServerPlayer) {
            if (instance.getKnownMovement().horizontalDistanceSqr() + (instance.getKnownMovement().y * instance.getKnownMovement().y) == 0
                    && this.teleportingUsingReturnScroll && this.returnScrollTeleportCountDown > 0
            ) {
                this.returnScrollTeleportCountDown--;
            } else {
                this.returnScrollTeleportCountDown = 100;
                this.teleportingUsingReturnScroll = false;
            }
        }

        // 回城卷轴的传送逻辑
        if (this.teleportingUsingReturnScroll && returnScrollTeleportCountDown == 0) {
            if (instance.level() instanceof ServerLevel serverLevel
                    && instance instanceof ServerPlayer serverPlayer
                    && serverPlayer.getRespawnConfig() != null
                    && serverPlayer.getRespawnConfig().respawnData() != null
            ) {
                BlockPos pos = serverPlayer.getRespawnConfig().respawnData().pos();
                boolean bl = serverLevel.getBlockState(pos).tags().anyMatch(Predicate.isEqual(BlockTags.BEDS));
                boolean bl2 = serverLevel.getBlockState(pos).is(Blocks.RESPAWN_ANCHOR);

                if (bl || bl2) {
                    serverPlayer.teleportTo(
                            serverLevel, pos.getCenter().x, pos.getCenter().y + 0.5, pos.getCenter().z,
                            Set.of(), 0.0F, 0.0F, true
                    );
                }
            }
        }
    }

    @Inject(method = "isSecondaryUseActive", at = @At("HEAD"), cancellable = true)
    public void shouldCancelInteraction(CallbackInfoReturnable<Boolean> cir) {
        Player instance = (Player) (Object) this;
        cir.setReturnValue(instance.isShiftKeyDown() || instance.isBlocking());
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    public void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("SelfRescuePlatformCooldown", this.selfRescuePlatformCooldown);
        valueOutput.putInt("ReturnScrollTeleportCountDown", this.returnScrollTeleportCountDown);
        valueOutput.putBoolean("TeleportingUsingReturnScroll", this.teleportingUsingReturnScroll);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"))
    public void readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
        this.selfRescuePlatformCooldown = valueInput.getIntOr("SelfRescuePlatformCooldown", 0);
        this.returnScrollTeleportCountDown = valueInput.getIntOr("ReturnScrollTeleportCountDown", 100);
        this.teleportingUsingReturnScroll = valueInput.getBooleanOr("TeleportingUsingReturnScroll", false);
    }

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
    @Unique
    public void AhhPvP$setSelfRescuePlatformCooldown(int cooldown) {
        this.selfRescuePlatformCooldown = cooldown;
    }

    @Override
    @Unique
    public int AhhPvP$getSelfRescuePlatformCooldown() {
        return this.selfRescuePlatformCooldown;
    }

    @Override
    @Unique
    public void AhhPvP$setReturnScrollTeleportCountDown(int countDown) {
        this.returnScrollTeleportCountDown = countDown;
    }

    @Override
    @Unique
    public int AhhPvP$getReturnScrollTeleportCountDown() {
        return this.returnScrollTeleportCountDown;
    }

    @Override
    @Unique
    public void AhhPvP$setTeleportingUsingReturnScroll(boolean isTeleportingUsingReturnScroll) {
        this.teleportingUsingReturnScroll = isTeleportingUsingReturnScroll;
    }

    @Override
    @Unique
    public boolean AhhPvP$isTeleportingUsingReturnScroll() {
        return this.teleportingUsingReturnScroll;
    }
}