package com.aiden.pvp.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class ChickenDefenseEntityRenderState extends LivingEntityRenderState {
    public float flap;
    public float flapSpeed;
}
