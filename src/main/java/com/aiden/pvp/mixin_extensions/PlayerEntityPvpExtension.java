package com.aiden.pvp.mixin_extensions;

import com.aiden.pvp.entities.FishingBobberEntity;

public interface PlayerEntityPvpExtension {
    void AhhPvP$setPvpFishHook(FishingBobberEntity fishingBobberEntity);
    FishingBobberEntity AhhPvP$getPvpFishHook();

    void AhhPvP$setSelfRescuePlatformCooldown(int cooldown);
    int AhhPvP$getSelfRescuePlatformCooldown();

    void AhhPvP$setReturnScrollTeleportCountDown(int countDown);
    int AhhPvP$getReturnScrollTeleportCountDown();

    void AhhPvP$setTeleportingUsingReturnScroll(boolean isTeleportingUsingReturnScroll);
    boolean AhhPvP$isTeleportingUsingReturnScroll();
}
