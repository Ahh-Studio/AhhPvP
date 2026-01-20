package com.aiden.pvp.client.render.entity.state;

import com.aiden.pvp.entities.MurdererEntity;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Arm;

public class MurdererEntityRenderState extends BipedEntityRenderState {
    public boolean hasVehicle;
    public boolean attacking;
    public Arm mainArm = Arm.RIGHT;
    public MurdererEntity.State murdererState = MurdererEntity.State.NEUTRAL;
    public int crossbowPullTime;
    public float itemUseTime;
    public float handSwingProgress;
}
