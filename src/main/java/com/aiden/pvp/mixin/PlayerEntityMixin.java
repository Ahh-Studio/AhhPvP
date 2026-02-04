package com.aiden.pvp.mixin;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityPvpExtension {
    @Unique
    private FishingBobberEntity pvpFishHook = null;

    @Unique
    public boolean blocking = false;

    @Unique
    public int selfRescuePlatformCooldown = 0;

    @Unique
    public int returnScrollTeleportCountDown = 100;

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
        PlayerEntityPvpExtension extension = (PlayerEntityPvpExtension) instance;

        if (!instance.getMainHandItem().is(ModItems.FISHING_ROD) && !instance.getOffhandItem().is(ModItems.FISHING_ROD) && this.pvpFishHook != null) {
            this.pvpFishHook.discard();
            extension.AhhPvP$setPvpFishHook(null);
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
    }
}
