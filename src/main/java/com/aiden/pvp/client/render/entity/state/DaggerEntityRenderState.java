package com.aiden.pvp.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class DaggerEntityRenderState extends EntityRenderState {
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public Vec3 velocity = Vec3.ZERO;
}
