package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.client.render.entity.state.FishingBobberEntityRenderState;
import com.aiden.pvp.entities.FishingBobberEntity;
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
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity, FishingBobberEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/fishing_hook.png");
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

    private Vec3 getHandPos(Player player, float handRotation, float tickProgress) {
        int i = getArmHoldingRod(player) == HumanoidArm.RIGHT ? 1 : -1;
        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double l = 960.0 / this.entityRenderDispatcher.options.fov().get().intValue();
            Vec3 vec3d = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane(i * 0.525F, -0.1F).scale(l).yRot(handRotation * 0.5F).xRot(-handRotation * 0.7F);
            return player.getEyePosition(tickProgress).add(vec3d);
        } else {
            float f = Mth.lerp(tickProgress, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
            double d = Mth.sin(f);
            double e = Mth.cos(f);
            float g = player.getScale();
            double h = i * 0.35 * g;
            double j = 0.8 * g;
            float k = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(tickProgress).add(-e * h - d * j, k - 0.45 * g, -d * h + e * j);
        }
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

    public void updateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityRenderState fishingBobberEntityRenderState, float f) {
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
