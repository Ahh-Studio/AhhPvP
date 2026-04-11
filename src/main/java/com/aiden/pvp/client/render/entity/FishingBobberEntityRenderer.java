package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.client.render.entity.state.FishingBobberEntityRenderState;
import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.items.FishingRodItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity, FishingBobberEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/fishing/fishing_hook.png");
    private static final RenderType LAYER = RenderTypes.entityCutout(TEXTURE);

    public FishingBobberEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(FishingBobberEntity entity, Frustum frustum, double x, double y, double z) {
        return super.shouldRender(entity, frustum, x, y, z) && entity.getPlayerOwner() != null;
    }

    @Override
    public void submit(
            FishingBobberEntityRenderState fishingBobberEntityRenderState,
            PoseStack matrixStack,
            SubmitNodeCollector orderedRenderCommandQueue,
            CameraRenderState cameraRenderState
    ) {
        matrixStack.pushPose();
        matrixStack.pushPose();
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        matrixStack.mulPose(cameraRenderState.orientation);
        orderedRenderCommandQueue.submitCustomGeometry(matrixStack, LAYER, (matricesEntry, vertexConsumer) -> {
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityRenderState.lightCoords, 0.0F, 0, 0, 1);
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityRenderState.lightCoords, 1.0F, 0, 1, 1);
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityRenderState.lightCoords, 1.0F, 1, 1, 0);
            vertex(vertexConsumer, matricesEntry, fishingBobberEntityRenderState.lightCoords, 0.0F, 1, 0, 0);
        });
        matrixStack.popPose();
        float f = (float) fishingBobberEntityRenderState.pos.x;
        float g = (float) fishingBobberEntityRenderState.pos.y;
        float h = (float) fishingBobberEntityRenderState.pos.z;
        float i = Minecraft.getInstance().getWindow().getAppropriateLineWidth();
        orderedRenderCommandQueue.submitCustomGeometry(matrixStack, RenderTypes.lines(), (matricesEntry, vertexConsumer) -> {
            int j = 16;

            for (int k = 0; k < 16; k++) {
                float l = percentage(k, 16);
                float m = percentage(k + 1, 16);
                renderFishingLine(f, g, h, vertexConsumer, matricesEntry, l, m, i);
                renderFishingLine(f, g, h, vertexConsumer, matricesEntry, m, l, i);
            }
        });
        matrixStack.popPose();
        super.submit(fishingBobberEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public static HumanoidArm getArmHoldingRod(Player player) {
        return player.getMainHandItem().getItem() instanceof FishingRodItem ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    private Vec3 getHandPos(Player owner, float swing, float partialTicks) {
        int invert = getHoldingArm(owner) == HumanoidArm.RIGHT ? 1 : -1;
        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && owner == Minecraft.getInstance().player) {
            float fov = this.entityRenderDispatcher.options.fov().get().intValue();
            double viewBobbingScale = 960.0 / fov;
            Vec3 viewVec = this.entityRenderDispatcher
                    .camera
                    .getNearPlane(fov)
                    .getPointOnPlane(invert * 0.525F, -0.1F)
                    .scale(viewBobbingScale)
                    .yRot(swing * 0.5F)
                    .xRot(-swing * 0.7F);
            return owner.getEyePosition(partialTicks).add(viewVec);
        } else {
            float ownerYRot = Mth.lerp(partialTicks, owner.yBodyRotO, owner.yBodyRot) * (float) (Math.PI / 180.0);
            double sin = Mth.sin(ownerYRot);
            double cos = Mth.cos(ownerYRot);
            float playerScale = owner.getScale();
            double rightOffset = invert * 0.35 * playerScale;
            double forwardOffset = 0.8 * playerScale;
            float yOffset = owner.isCrouching() ? -0.1875F : 0.0F;
            return owner.getEyePosition(partialTicks)
                    .add(-cos * rightOffset - sin * forwardOffset, yOffset - 0.45 * playerScale, -sin * rightOffset + cos * forwardOffset);
        }
    }

    public static HumanoidArm getHoldingArm(final Player owner) {
        return owner.getMainHandItem().getItem() instanceof FishingRodItem ? owner.getMainArm() : owner.getMainArm().getOpposite();
    }

    private static float percentage(int value, int denominator) {
        return (float)value / denominator;
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose matrix, int light, float x, int y, int u, int v) {
        buffer.addVertex(matrix, x - 0.5F, y - 0.5F, 0.0F)
                .setColor(CommonColors.WHITE)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(matrix, 0.0F, 1.0F, 0.0F);
    }

    private static void renderFishingLine(
            float x, float y, float z, VertexConsumer buffer, PoseStack.Pose matrices, float segmentStart, float segmentEnd, float getMinimumLineWidth
    ) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
        float k = z * segmentEnd - h;
        float l = Mth.sqrt(i * i + j * j + k * k);
        i /= l;
        j /= l;
        k /= l;
        buffer.addVertex(matrices, f, g, h).setColor(CommonColors.BLACK).setNormal(matrices, i, j, k).setLineWidth(getMinimumLineWidth);
    }

    @Override
    public FishingBobberEntityRenderState createRenderState() {
        return new FishingBobberEntityRenderState();
    }

    @Override
    public void extractRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityRenderState fishingBobberEntityRenderState, float f) {
        super.extractRenderState(fishingBobberEntity, fishingBobberEntityRenderState, f);
        Player playerEntity = fishingBobberEntity.getPlayerOwner();
        if (playerEntity == null) {
            fishingBobberEntityRenderState.pos = Vec3.ZERO;
        } else {
            float g = playerEntity.getAttackAnim(f);
            float h = Mth.sin(Mth.sqrt(g) * (float) Math.PI);
            Vec3 vec3d = this.getHandPos(playerEntity, h, f);
            Vec3 vec3d2 = fishingBobberEntity.getPosition(f).add(0.0, 0.25, 0.0);
            fishingBobberEntityRenderState.pos = vec3d.subtract(vec3d2);
        }
    }

    @Override
    protected boolean affectedByCulling(FishingBobberEntity fishingBobberEntity) {
        return false;
    }
}
