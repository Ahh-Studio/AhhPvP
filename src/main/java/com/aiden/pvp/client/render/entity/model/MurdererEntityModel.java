package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.aiden.pvp.entities.MurdererEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class MurdererEntityModel extends EntityModel<MurdererEntityRenderState> implements ModelWithArms<MurdererEntityRenderState>, ModelWithHead {
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart arms;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public MurdererEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild(EntityModelPartNames.HEAD);
        this.hat = this.head.getChild(EntityModelPartNames.HAT);
        this.hat.visible = false;
        this.arms = modelPart.getChild(EntityModelPartNames.ARMS);
        this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(
                EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.origin(0.0F, 0.0F, 0.0F)
        );
        modelPartData2.addChild(
                EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new Dilation(0.45F)), ModelTransform.NONE
        );
        modelPartData2.addChild(
                EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.origin(0.0F, -2.0F, 0.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create()
                        .uv(16, 20)
                        .cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                        .uv(0, 38)
                        .cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new Dilation(0.5F)),
                ModelTransform.origin(0.0F, 0.0F, 0.0F)
        );
        ModelPartData modelPartData3 = modelPartData.addChild(
                EntityModelPartNames.ARMS,
                ModelPartBuilder.create().uv(44, 22).cuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).uv(40, 38).cuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                ModelTransform.of(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
        );
        modelPartData3.addChild("left_shoulder", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), ModelTransform.NONE);
        modelPartData.addChild(
                EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.origin(-2.0F, 12.0F, 0.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(2.0F, 12.0F, 0.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.RIGHT_ARM,
                ModelPartBuilder.create().uv(40, 46).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(-5.0F, 2.0F, 0.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.LEFT_ARM,
                ModelPartBuilder.create().uv(40, 46).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                ModelTransform.origin(5.0F, 2.0F, 0.0F)
        );
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(MurdererEntityRenderState state) {
        super.setAngles(state);
        this.head.yaw = state.relativeHeadYaw * (float) (Math.PI / 180.0);
        this.head.pitch = state.pitch * (float) (Math.PI / 180.0);
        if (state.hasVehicle) {
            this.rightArm.pitch = (float) (-Math.PI / 5);
            this.rightArm.yaw = 0.0F;
            this.rightArm.roll = 0.0F;
            this.leftArm.pitch = (float) (-Math.PI / 5);
            this.leftArm.yaw = 0.0F;
            this.leftArm.roll = 0.0F;
            this.rightLeg.pitch = -1.4137167F;
            this.rightLeg.yaw = (float) (Math.PI / 10);
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.4137167F;
            this.leftLeg.yaw = (float) (-Math.PI / 10);
            this.leftLeg.roll = -0.07853982F;
        } else {
            float f = state.limbSwingAmplitude;
            float g = state.limbSwingAnimationProgress;
            this.rightArm.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 2.0F * f * 0.5F;
            this.rightArm.yaw = 0.0F;
            this.rightArm.roll = 0.0F;
            this.leftArm.pitch = MathHelper.cos(g * 0.6662F) * 2.0F * f * 0.5F;
            this.leftArm.yaw = 0.0F;
            this.leftArm.roll = 0.0F;
            this.rightLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f * 0.5F;
            this.rightLeg.yaw = 0.0F;
            this.rightLeg.roll = 0.0F;
            this.leftLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f * 0.5F;
            this.leftLeg.yaw = 0.0F;
            this.leftLeg.roll = 0.0F;
        }

        MurdererEntity.State state2 = state.murdererState;
        if (state2 == MurdererEntity.State.ATTACKING) {
            if (state.getMainHandItemState().isEmpty()) {
                ArmPosing.zombieArms(this.leftArm, this.rightArm, true, state);
            } else {
                ArmPosing.meleeAttack(this.rightArm, this.leftArm, state.mainArm, state.handSwingProgress, state.age);
            }
        } else if (state2 == MurdererEntity.State.SPELLCASTING) {
            this.rightArm.originZ = 0.0F;
            this.rightArm.originX = -5.0F;
            this.leftArm.originZ = 0.0F;
            this.leftArm.originX = 5.0F;
            this.rightArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.25F;
            this.leftArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.25F;
            this.rightArm.roll = (float) (Math.PI * 3.0 / 4.0);
            this.leftArm.roll = (float) (-Math.PI * 3.0 / 4.0);
            this.rightArm.yaw = 0.0F;
            this.leftArm.yaw = 0.0F;
        } else if (state2 == MurdererEntity.State.BOW_AND_ARROW) {
            this.rightArm.yaw = -0.1F + this.head.yaw;
            this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
            this.leftArm.pitch = -0.9424779F + this.head.pitch;
            this.leftArm.yaw = this.head.yaw - 0.4F;
            this.leftArm.roll = (float) (Math.PI / 2);
        } else if (state2 == MurdererEntity.State.CROSSBOW_HOLD) {
            ArmPosing.hold(this.rightArm, this.leftArm, this.head, true);
        } else if (state2 == MurdererEntity.State.CROSSBOW_CHARGE) {
            ArmPosing.charge(this.rightArm, this.leftArm, state.crossbowPullTime, state.itemUseTime, true);
        } else if (state2== MurdererEntity.State.CELEBRATING) {
            this.rightArm.originZ = 0.0F;
            this.rightArm.originX = -5.0F;
            this.rightArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.05F;
            this.rightArm.roll = 2.670354F;
            this.rightArm.yaw = 0.0F;
            this.leftArm.originZ = 0.0F;
            this.leftArm.originX = 5.0F;
            this.leftArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.05F;
            this.leftArm.roll = (float) (-Math.PI * 3.0 / 4.0);
            this.leftArm.yaw = 0.0F;
        }

        boolean bl = state2 == MurdererEntity.State.CROSSED;
        this.arms.visible = bl;
        this.leftArm.visible = !bl;
        this.rightArm.visible = !bl;
    }

    private ModelPart getAttackingArm(Arm arm) {
        return arm == Arm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(MurdererEntityRenderState murdererEntityRenderState, Arm arm, MatrixStack matrixStack) {
        this.root.applyTransform(matrixStack);
        this.getAttackingArm(arm).applyTransform(matrixStack);
    }
}
