package com.aiden.pvp.client.render.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DaggerEntityRenderState extends EntityRenderState {
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    public Vec3 velocity = Vec3.ZERO;
}
