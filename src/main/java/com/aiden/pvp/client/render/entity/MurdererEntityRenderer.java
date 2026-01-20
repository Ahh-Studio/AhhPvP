package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.client.render.entity.model.MurdererEntityModel;
import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.aiden.pvp.entities.MurdererEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MurdererEntityRenderer extends BipedEntityRenderer<MurdererEntity, MurdererEntityRenderState, MurdererEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(PvP.MOD_ID, "textures/entity/murderer.png");

    public MurdererEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MurdererEntityModel(ctx.getPart(ModEntityModelLayers.MURDERER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this) {
            @Override
            public void render(
                    MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                    MurdererEntityRenderState state, float limbSwing, float limbSwingAmount
            ) {
                if (state.attacking || !state.getMainHandItemState().isEmpty()) {
                    super.render(matrices, queue, light, state, limbSwing, limbSwingAmount);
                }
            }
        });
        this.addFeature(
                new ArmorFeatureRenderer<>(
                        this,
                        EquipmentModelData.mapToEntityModel(EntityModelLayers.PLAYER_EQUIPMENT, ctx.getEntityModels(), MurdererEntityModel::new),
                        ctx.getEquipmentRenderer())
        );
    }

    public void updateRenderState(MurdererEntity murdererEntity, MurdererEntityRenderState murdererEntityRenderState, float f) {
        super.updateRenderState(murdererEntity, murdererEntityRenderState, f);
        BipedEntityRenderState.updateRenderState(murdererEntity, murdererEntityRenderState, this.itemModelResolver);
        murdererEntityRenderState.hasVehicle = murdererEntity.hasVehicle();
        murdererEntityRenderState.mainArm = murdererEntity.getMainArm();
        murdererEntityRenderState.attacking = murdererEntity.isAttacking();
        murdererEntityRenderState.handSwingProgress = murdererEntity.getHandSwingProgress(f);
    }

    @Override
    public Identifier getTexture(MurdererEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public MurdererEntityRenderState createRenderState() {
        return new MurdererEntityRenderState();
    }
}
