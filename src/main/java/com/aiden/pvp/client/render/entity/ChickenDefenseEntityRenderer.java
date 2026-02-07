package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.model.ChickenDefenseEntityModel;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.client.render.entity.state.ChickenDefenseEntityRenderState;
import com.aiden.pvp.entities.ChickenDefenseEntity;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.animal.chicken.ChickenModel;
import net.minecraft.client.model.animal.chicken.ColdChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;

import java.util.Map;

public class ChickenDefenseEntityRenderer extends MobRenderer<ChickenDefenseEntity, ChickenDefenseEntityRenderState, ChickenDefenseEntityModel> {
    public ChickenDefenseEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenDefenseEntityModel(context.bakeLayer(ModEntityModelLayers.CHICKEN_DEFENSE)), 0.3F);
    }

    public Identifier getTextureLocation(ChickenDefenseEntityRenderState chickenRenderState) {
        return Identifier.fromNamespaceAndPath(PvP.MOD_ID, "textures/entity/chicken_defense.png");
    }

    public ChickenDefenseEntityRenderState createRenderState() {
        return new ChickenDefenseEntityRenderState();
    }

    public void extractRenderState(ChickenDefenseEntity chicken, ChickenDefenseEntityRenderState chickenRenderState, float f) {
        super.extractRenderState(chicken, chickenRenderState, f);
        chickenRenderState.flap = Mth.lerp(f, chicken.oFlap, chicken.flap);
        chickenRenderState.flapSpeed = Mth.lerp(f, chicken.oFlapSpeed, chicken.flapSpeed);
    }
}
