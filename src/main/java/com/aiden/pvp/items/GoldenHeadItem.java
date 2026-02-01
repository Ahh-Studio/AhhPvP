package com.aiden.pvp.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class GoldenHeadItem extends BlockItem {
    public GoldenHeadItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        user.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
        user.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
        user.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0));
        user.getItemInHand(hand).consume(1, user);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player user = context.getPlayer();
        if (user != null) {
            user.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
            user.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
            user.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0));
            user.getItemInHand(context.getHand()).consume(1, user);
        }
        return InteractionResult.SUCCESS;
    }
}
