package com.aiden.pvp.client.render.entity.state;

import com.aiden.pvp.entities.MurdererEntity;
import net.minecraft.client.renderer.entity.state.UndeadRenderState;
import net.minecraft.world.entity.HumanoidArm;

public class MurdererEntityRenderState extends UndeadRenderState {
    public boolean hasVehicle;
    public boolean attacking;
    public HumanoidArm mainArm = HumanoidArm.RIGHT;
    public MurdererEntity.State murdererState = MurdererEntity.State.NEUTRAL;
    public int crossbowPullTime;
    public float itemUseTime;
    public float handSwingProgress;
}
