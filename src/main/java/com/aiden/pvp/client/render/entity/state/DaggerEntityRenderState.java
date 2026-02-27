package com.aiden.pvp.client.render.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.phys.Vec3;

public class DaggerEntityRenderState extends EntityRenderState {
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public Vec3 velocity = Vec3.ZERO;
}
