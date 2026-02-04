package com.aiden.pvp.items;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;

public class SwordItem extends Item {
    public SwordItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null) {
            return ((PlayerEntityPvpExtension) context.getPlayer()).isBlocking() ? InteractionResult.FAIL : super.useOn(context);
        } else return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        return ((PlayerEntityPvpExtension) user).isBlocking() ? InteractionResult.FAIL : super.interactLivingEntity(stack, user, entity, hand);
    }
}
