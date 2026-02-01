package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class MurdererEntityModel extends HumanoidModel<MurdererEntityRenderState> implements ArmedModel<MurdererEntityRenderState>, HeadedModel {
    private final net.minecraft.client.model.geom.ModelPart head;
    private final net.minecraft.client.model.geom.ModelPart body;
    private final net.minecraft.client.model.geom.ModelPart leftLeg;
    private final net.minecraft.client.model.geom.ModelPart rightLeg;
    private final net.minecraft.client.model.geom.ModelPart rightArm;
    private final net.minecraft.client.model.geom.ModelPart leftArm;

    public MurdererEntityModel(net.minecraft.client.model.geom.ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(PartNames.HEAD);
        this.body = modelPart.getChild(PartNames.BODY);
        this.leftLeg = modelPart.getChild(PartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(PartNames.RIGHT_LEG);
        this.leftArm = modelPart.getChild(PartNames.LEFT_ARM);
        this.rightArm = modelPart.getChild(PartNames.RIGHT_ARM);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        head.addOrReplaceChild(
                PartNames.HAT,
                CubeListBuilder.create(),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                PartNames.RIGHT_ARM,
                CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );
        root.addOrReplaceChild(
                PartNames.LEFT_ARM,
                CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        root.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-1.9F, 12.0F, 0.0F)
        );
        root.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(1.9F, 12.0F, 0.0F)
        );
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(MurdererEntityRenderState state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.body.yRot = 0.0F;
        this.body.xRot = 0.0F;

        if (state.hasVehicle) {
            this.rightArm.xRot = -1.309F;
            this.leftArm.xRot = -1.309F;
            this.rightLeg.xRot = -1.414F;
            this.leftLeg.xRot = -1.414F;
        } else {
            float swing = state.walkAnimationPos;
            float swingAmount = state.walkAnimationSpeed;

            this.rightArm.xRot = Mth.cos(swing * 0.6662F + Mth.PI) * 2.0F * swingAmount * 0.5F;
            this.leftArm.xRot = Mth.cos(swing * 0.6662F) * 2.0F * swingAmount * 0.5F;

            if (swingAmount < 0.1F) {
                this.rightArm.xRot = 0.0F;
                this.leftArm.xRot = 0.0F;
            }

            this.rightLeg.xRot = Mth.cos(swing * 0.6662F) * 1.4F * swingAmount;
            this.leftLeg.xRot = Mth.cos(swing * 0.6662F + Mth.PI) * 1.4F * swingAmount;
        }

        if (state.attacking) {
            AnimationUtils.bobModelPart(state.mainArm == HumanoidArm.LEFT ? this.leftArm : this.rightArm, state.ageInTicks, state.mainArm == HumanoidArm.LEFT ? -1.0F : 1.0F);
        }
    }

    private net.minecraft.client.model.geom.ModelPart getAttackingArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public net.minecraft.client.model.geom.ModelPart getBody() {
        return this.body;
    }

    @Override
    public net.minecraft.client.model.geom.ModelPart getHead() {
        return this.head;
    }

    @Override
    public void translateToHand(MurdererEntityRenderState murdererEntityRenderState, HumanoidArm arm, PoseStack matrixStack) {
        this.root.translateAndRotate(matrixStack);
        (arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm).translateAndRotate(matrixStack);
    }
}
