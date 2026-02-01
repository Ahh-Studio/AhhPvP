package com.aiden.pvp.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderState extends EntityRenderState {
    public Vec3 pos = Vec3.ZERO;
}
