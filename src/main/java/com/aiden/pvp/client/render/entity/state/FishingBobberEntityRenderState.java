package com.aiden.pvp.client.render.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FishingBobberEntityRenderState extends EntityRenderState {
    public Vec3 pos = Vec3.ZERO;
}
