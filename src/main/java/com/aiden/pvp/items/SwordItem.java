package com.aiden.pvp.items;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class SwordItem extends Item {
    public SwordItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null) {
            return ((PlayerEntityPvpExtension) context.getPlayer()).isBlocking() ? ActionResult.FAIL : super.useOnBlock(context);
        } else return ActionResult.FAIL;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ((PlayerEntityPvpExtension) user).isBlocking() ? ActionResult.FAIL : super.useOnEntity(stack, user, entity, hand);
    }
}
