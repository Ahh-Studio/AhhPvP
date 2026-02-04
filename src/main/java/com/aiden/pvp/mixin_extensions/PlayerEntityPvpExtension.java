package com.aiden.pvp.mixin_extensions;

import com.aiden.pvp.entities.FishingBobberEntity;

public interface PlayerEntityPvpExtension {
    void setPvpFishHook(FishingBobberEntity fishingBobberEntity);
    FishingBobberEntity getPvpFishHook();

    void setBlocking(boolean bl);
    boolean isBlocking();

    void setSelfRescuePlatformCooldown(int cooldown);
    int getSelfRescuePlatformCooldown();
}
