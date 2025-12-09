package com.aiden.pvp.items;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GoldenHeadItem extends BlockItem {
    public GoldenHeadItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 1));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0));
        user.getStackInHand(hand).decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();
        if (user != null) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 1));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0));
            user.getStackInHand(context.getHand()).decrementUnlessCreative(1, user);
        }
        return ActionResult.SUCCESS;
    }
}
