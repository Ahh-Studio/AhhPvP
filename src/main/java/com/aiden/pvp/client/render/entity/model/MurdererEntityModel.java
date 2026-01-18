package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class MurdererEntityModel extends BipedEntityModel<MurdererEntityRenderState> implements ModelWithArms<MurdererEntityRenderState>, ModelWithHead {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public MurdererEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.body = modelPart.getChild(EntityModelPartNames.BODY);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData head = root.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                ModelTransform.origin(0.0F, 0.0F, 0.0F)
        );

        head.addChild(
                EntityModelPartNames.HAT,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );
        root.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                ModelTransform.origin(0.0F, 0.0F, 0.0F)
        );
        root.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                ModelTransform.origin(0.0F, 0.0F, 0.0F)
        );
        root.addChild(
                EntityModelPartNames.RIGHT_ARM,
                ModelPartBuilder.create().uv(40, 16).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(-5.0F, 2.0F, 0.0F)
        );
        root.addChild(
                EntityModelPartNames.LEFT_ARM,
                ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(5.0F, 2.0F, 0.0F)
        );
        root.addChild(
                EntityModelPartNames.RIGHT_LEG,
                ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(-1.9F, 12.0F, 0.0F)
        );
        root.addChild(
                EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(1.9F, 12.0F, 0.0F)
        );
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(MurdererEntityRenderState state) {
        super.setAngles(state);
        this.head.yaw = state.relativeHeadYaw * MathHelper.RADIANS_PER_DEGREE;
        this.head.pitch = state.pitch * MathHelper.RADIANS_PER_DEGREE;
        this.body.yaw = 0.0F;
        this.body.pitch = 0.0F;

        if (state.hasVehicle) {
            this.rightArm.pitch = -1.309F;
            this.leftArm.pitch = -1.309F;
            this.rightLeg.pitch = -1.414F;
            this.leftLeg.pitch = -1.414F;
        } else {
            float swing = state.limbSwingAnimationProgress;
            float swingAmount = state.limbSwingAmplitude;

            this.rightArm.pitch = MathHelper.cos(swing * 0.6662F + MathHelper.PI) * 2.0F * swingAmount * 0.5F;
            this.leftArm.pitch = MathHelper.cos(swing * 0.6662F) * 2.0F * swingAmount * 0.5F;

            if (swingAmount < 0.1F) {
                this.rightArm.pitch = 0.0F;
                this.leftArm.pitch = 0.0F;
            }

            this.rightLeg.pitch = MathHelper.cos(swing * 0.6662F) * 1.4F * swingAmount;
            this.leftLeg.pitch = MathHelper.cos(swing * 0.6662F + MathHelper.PI) * 1.4F * swingAmount;
        }

        if (state.attacking) {
            ArmPosing.swingArm(state.mainArm == Arm.LEFT ? this.leftArm : this.rightArm, state.age, state.mainArm == Arm.LEFT ? -1.0F : 1.0F);
        }
    }

    private ModelPart getAttackingArm(Arm arm) {
        return arm == Arm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getBody() {
        return this.body;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(MurdererEntityRenderState murdererEntityRenderState, Arm arm, MatrixStack matrixStack) {
        this.root.applyTransform(matrixStack);
        (arm == Arm.RIGHT ? this.rightArm : this.leftArm).applyTransform(matrixStack);
    }
}
