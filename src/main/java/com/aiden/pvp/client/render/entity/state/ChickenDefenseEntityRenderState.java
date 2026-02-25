package com.aiden.pvp.client.render.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenDefenseEntityRenderState extends LivingEntityRenderState {
    public float flap;
    public float flapSpeed;
}
