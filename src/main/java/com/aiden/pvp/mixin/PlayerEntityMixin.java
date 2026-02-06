package com.aiden.pvp.mixin;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
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

import java.util.EnumSet;
import java.util.Set;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityPvpExtension {
    private @Unique FishingBobberEntity pvpFishHook = null;
    public @Unique int selfRescuePlatformCooldown = 0;
    public @Unique int returnScrollTeleportCountDown = 100;
    public @Unique boolean teleportingUsingReturnScroll = false;

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
                if (serverPlayer.getRespawnConfig() != null && (serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BLACK_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BLUE_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BROWN_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.CYAN_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.GRAY_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.GREEN_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.LIGHT_BLUE_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.LIME_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.LIGHT_GRAY_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.MAGENTA_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.ORANGE_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.PINK_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.PURPLE_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.RED_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.WHITE_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.YELLOW_BED)
                        || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.RESPAWN_ANCHOR))) {
                    serverPlayer.teleportTo(
                            serverLevel,
                            serverPlayer.getRespawnConfig().respawnData().pos().getCenter().x,
                            serverPlayer.getRespawnConfig().respawnData().pos().getCenter().y + 0.5,
                            serverPlayer.getRespawnConfig().respawnData().pos().getCenter().z,
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
