package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.client.render.entity.state.DaggerEntityRenderState;
import com.aiden.pvp.entities.DaggerEntity;
import com.aiden.pvp.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class DaggerEntityRenderer<T extends DaggerEntity> extends EntityRenderer<T, DaggerEntityRenderState> {
    private final ItemModelResolver itemModelManager;
    private final float scale;
    private final boolean lit;

    public DaggerEntityRenderer(EntityRendererProvider.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelResolver();
        this.scale = scale;
        this.lit = lit;
    }

    public DaggerEntityRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0F, false);
    }

    @Override
    protected int getBlockLightLevel(T entity, @NonNull BlockPos pos) {
        return this.lit ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void submit(
            DaggerEntityRenderState daggerEntityRenderState,
            PoseStack matrixStack,
            SubmitNodeCollector orderedRenderCommandQueue,
            CameraRenderState cameraRenderState
    ) {
        matrixStack.pushPose();

        matrixStack.translate(0.0F, 0.25F, 0.0F);
        matrixStack.scale(this.scale, this.scale, this.scale);

        Vec3 velocity = daggerEntityRenderState.velocity;
        if (velocity.lengthSqr() > 0.001) {
            float yaw = (float) (Mth.atan2(velocity.x(), velocity.z()) * 180.0 / Math.PI) - 90.0F;
            matrixStack.mulPose(Axis.YP.rotationDegrees(yaw - 135.0F));
        }

        matrixStack.mulPose(Axis.XP.rotationDegrees(95.0F));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(0.0F));

        daggerEntityRenderState.itemRenderState
                .submit(matrixStack, orderedRenderCommandQueue, daggerEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, daggerEntityRenderState.outlineColor);

        matrixStack.popPose();
        super.submit(daggerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public DaggerEntityRenderState createRenderState() {
        return new DaggerEntityRenderState();
    }

    @Override
    public void extractRenderState(T entity, DaggerEntityRenderState daggerEntityRenderState, float f) {
        super.extractRenderState(entity, daggerEntityRenderState, f);
        daggerEntityRenderState.velocity = entity.getDeltaMovement().multiply(-1, 1, -1);
        this.itemModelManager.updateForNonLiving(
                daggerEntityRenderState.itemRenderState,
                new ItemStack(ModItems.THROWABLE_DAGGER),
                ItemDisplayContext.GROUND,
                entity
        );
    }
}
