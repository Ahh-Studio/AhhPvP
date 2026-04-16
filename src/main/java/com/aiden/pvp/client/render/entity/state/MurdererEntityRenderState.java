package com.aiden.pvp.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;

@Environment(EnvType.CLIENT)
public class MurdererEntityRenderState extends HumanoidRenderState {
    public boolean hasVehicle;
    public boolean attacking;
    public HumanoidArm mainArm = HumanoidArm.RIGHT;
    public float handSwingProgress;
}
