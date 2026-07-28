package com.aiden.pvp.client.render.entity.model;

import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class MurdererEntityModel extends HumanoidModel<MurdererEntityRenderState> implements ArmedModel<MurdererEntityRenderState>, HeadedModel {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public MurdererEntityModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityTranslucent);
        this.head = modelPart.getChild(PartNames.HEAD);
        this.body = modelPart.getChild(PartNames.BODY);
        this.leftLeg = modelPart.getChild(PartNames.LEFT_LEG);
        this.rightLeg = modelPart.getChild(PartNames.RIGHT_LEG);
        this.leftArm = modelPart.getChild(PartNames.LEFT_ARM);
        this.rightArm = modelPart.getChild(PartNames.RIGHT_ARM);
    }

    public static LayerDefinition getTexturedModelData() {
        // 与原版 PlayerModel 相同：第二层（外套/袖子/裤腿）比基础层大 0.25 像素，避免与装备层穿模
        CubeDeformation overlayScale = new CubeDeformation(0.25F);

        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        // 基础层（皮肤底层）
        PartDefinition head = root.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // 第二层：头部外层（HAT）——使用贴图 X 偏移 32 的"帽子层"
        head.addOrReplaceChild(
                PartNames.HAT,
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, overlayScale),
                PartPose.ZERO
        );

        PartDefinition body = root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // 第二层：身体外套（jacket）
        body.addOrReplaceChild(
                "jacket",
                CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, overlayScale),
                PartPose.ZERO
        );

        PartDefinition rightArm = root.addOrReplaceChild(
                PartNames.RIGHT_ARM,
                CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );

        // 第二层：右臂外套（right_sleeve）
        rightArm.addOrReplaceChild(
                "right_sleeve",
                CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, overlayScale),
                PartPose.ZERO
        );

        PartDefinition leftArm = root.addOrReplaceChild(
                PartNames.LEFT_ARM,
                CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );

        // 第二层：左臂外套（left_sleeve）
        leftArm.addOrReplaceChild(
                "left_sleeve",
                CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, overlayScale),
                PartPose.ZERO
        );

        PartDefinition rightLeg = root.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-1.9F, 12.0F, 0.0F)
        );

        // 第二层：右腿外层（right_pants）
        rightLeg.addOrReplaceChild(
                "right_pants",
                CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, overlayScale),
                PartPose.ZERO
        );

        PartDefinition leftLeg = root.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(1.9F, 12.0F, 0.0F)
        );

        // 第二层：左腿外层（left_pants）—— UV 使用贴图 X=0, Y=48，与原版 PlayerModel 一致
        leftLeg.addOrReplaceChild(
                "left_pants",
                CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, overlayScale),
                PartPose.ZERO
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

    private ModelPart getAttackingArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getBody() {
        return this.body;
    }

    @Override
    public @NonNull ModelPart getHead() {
        return this.head;
    }

    @Override
    public void translateToHand(MurdererEntityRenderState murdererEntityRenderState, HumanoidArm arm, PoseStack matrixStack) {
        this.root.translateAndRotate(matrixStack);
        (arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm).translateAndRotate(matrixStack);
    }
}
