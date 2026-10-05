package com.aiden.pvp.client.render.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.state.MurdererEntityRenderState;
import com.aiden.pvp.entities.murderer.MurdererEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class MurdererEntityRenderer extends LivingEntityRenderer<MurdererEntity, AvatarRenderState, PlayerModel> {
    private static final PlayerSkin SKIN;

    static {
        ClientAsset.ResourceTexture skinTexture = new ClientAsset.ResourceTexture(
                Identifier.fromNamespaceAndPath(PvP.MOD_ID, "entity/murderer")
        );
        SKIN = PlayerSkin.insecure(skinTexture, null, null, PlayerModelType.WIDE);
    }

    public MurdererEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, ctx.getModelSet(), part -> new PlayerModel(part, false)),
                ctx.getEquipmentRenderer()
        ));
        this.addLayer(new CapeLayer(this, ctx.getModelSet(), ctx.getEquipmentAssets()));
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), ctx.getPlayerSkinRenderCache()));
        this.addLayer(new WingsLayer<>(this, ctx.getModelSet(), ctx.getEquipmentRenderer()));
    }

    @Override
    protected boolean shouldShowName(MurdererEntity entity, double distanceToCameraSq) {
        return true;
    }

    @Override
    public void extractRenderState(MurdererEntity entity, AvatarRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTicks, this.itemModelResolver);
        state.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        state.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        state.skin = SKIN;
        state.showHat = true;
        state.showJacket = true;
        state.showLeftPants = true;
        state.showRightPants = true;
        state.showLeftSleeve = true;
        state.showRightSleeve = true;
        state.showCape = false;
        state.isSpectator = false;
        state.arrowCount = 0;
        state.stingerCount = 0;

        if (state instanceof MurdererEntityRenderState murdererState) {
            murdererState.attacking = entity.isAggressive();
            murdererState.mainArm = entity.getMainArm();
            murdererState.handSwingProgress = entity.getSwingAnimation(partialTicks);
        }
    }

    @Override
    public @NonNull Identifier getTextureLocation(AvatarRenderState state) {
        return state.skin.body().texturePath();
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new MurdererEntityRenderState();
    }
}