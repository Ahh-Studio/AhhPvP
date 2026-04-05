package com.aiden.pvp.client.item_properties_conditional;

import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record FishingRodCast() implements ConditionalItemModelProperty {
    public static final MapCodec<FishingRodCast> MAP_CODEC = MapCodec.unit(new FishingRodCast());

    @Override
    public @NonNull MapCodec<FishingRodCast> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(@NonNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, @NonNull ItemDisplayContext itemDisplayContext) {
        if (livingEntity instanceof Player player) {
            PlayerEntityPvpExtension playerExt = (PlayerEntityPvpExtension) player;
            if (playerExt.AhhPvP$getPvpFishHook() != null) {
                HumanoidArm humanoidArm = FishingBobberEntityRenderer.getArmHoldingRod(player);
                return player.getItemHeldByArm(humanoidArm).is(ModItems.FISHING_ROD);
            } else return false;
        } else return false;
    }
}
