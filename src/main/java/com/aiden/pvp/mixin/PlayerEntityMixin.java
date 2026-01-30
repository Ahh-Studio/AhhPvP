package com.aiden.pvp.mixin;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityPvpExtension {
    @Unique
    private FishingBobberEntity pvpFishHook = null;
    @Unique
    public boolean blocking = false;

    @Override
    @Unique
    public void setPvpFishHook(FishingBobberEntity fishingBobberEntity) {
        pvpFishHook = fishingBobberEntity;
    }

    @Override
    @Unique
    public FishingBobberEntity getPvpFishHook() {
        return this.pvpFishHook;
    }

    @Override
    @Unique
    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    @Unique
    public boolean isBlocking() {
        return this.blocking;
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
        PlayerEntity instance = (PlayerEntity) (Object) this;
        PlayerEntityPvpExtension extension = (PlayerEntityPvpExtension) instance;

        if (!instance.getMainHandStack().isOf(ModItems.FISHING_ROD) && !instance.getOffHandStack().isOf(ModItems.FISHING_ROD) && this.pvpFishHook != null) {
            this.pvpFishHook.discard();
            extension.setPvpFishHook(null);
        }
    }

    @Inject(
            method = "shouldCancelInteraction",
            at = @At("HEAD"),
            cancellable = true
    )
    public void shouldCancelInteraction(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity instance = (PlayerEntity) (Object) this;
        cir.setReturnValue(instance.isSneaking() || instance.isBlocking());
    }
}
