package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.model.ModEntityModelLayers;
import com.aiden.pvp.client.render.entity.model.MurdererEntityModel;
import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.aiden.pvp.entities.MurdererEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class MurdererEntityRenderer extends HumanoidMobRenderer<MurdererEntity, MurdererEntityRenderState, MurdererEntityModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "textures/entity/murderer.png");

    public MurdererEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MurdererEntityModel(ctx.bakeLayer(ModEntityModelLayers.MURDERER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this) {
            @Override
            public void submit(
                    PoseStack matrices, SubmitNodeCollector queue, int light,
                    MurdererEntityRenderState state, float limbSwing, float limbSwingAmount
            ) {
                if (state.attacking || !state.getMainHandItemState().isEmpty()) {
                    super.submit(matrices, queue, light, state, limbSwing, limbSwingAmount);
                }
            }
        });
        this.addLayer(
                new HumanoidArmorLayer<>(
                        this,
                        ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, ctx.getModelSet(), MurdererEntityModel::new),
                        ctx.getEquipmentRenderer())
        );
    }

    public void updateRenderState(MurdererEntity murdererEntity, MurdererEntityRenderState murdererEntityRenderState, float f) {
        super.extractRenderState(murdererEntity, murdererEntityRenderState, f);
        ArmedEntityRenderState.extractArmedEntityRenderState(murdererEntity, murdererEntityRenderState, this.itemModelResolver, f);
        murdererEntityRenderState.hasVehicle = murdererEntity.isPassenger();
        murdererEntityRenderState.mainArm = murdererEntity.getMainArm();
        murdererEntityRenderState.attacking = murdererEntity.isAggressive();
        murdererEntityRenderState.handSwingProgress = murdererEntity.getAttackAnim(f);
    }

    @Override
    public @NonNull Identifier getTextureLocation(MurdererEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public MurdererEntityRenderState createRenderState() {
        return new MurdererEntityRenderState();
    }
}
