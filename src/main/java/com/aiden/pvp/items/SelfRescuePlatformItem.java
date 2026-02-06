package com.aiden.pvp.items;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import java.util.ArrayList;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SelfRescuePlatformItem extends Item {
    public SelfRescuePlatformItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        PlayerEntityPvpExtension playerEntityPvpExtension = (PlayerEntityPvpExtension) user;
        if (playerEntityPvpExtension.AhhPvP$getSelfRescuePlatformCooldown() <= 0) {
            ItemStack itemStack = user.getItemInHand(hand);

            ArrayList<BlockPos> blockPos = new ArrayList<>();
            blockPos.add(blockPos(user, 0, 0));
            blockPos.add(blockPos(user, 1, 0));
            blockPos.add(blockPos(user, -1, 0));
            blockPos.add(blockPos(user, 0, 1));
            blockPos.add(blockPos(user, 1, 1));
            blockPos.add(blockPos(user, -1, 1));
            blockPos.add(blockPos(user, 0, -1));
            blockPos.add(blockPos(user, 1, -1));
            blockPos.add(blockPos(user, -1, -1));
            blockPos.add(blockPos(user, 2, 1));
            blockPos.add(blockPos(user, 2, -1));
            blockPos.add(blockPos(user, -2, 1));
            blockPos.add(blockPos(user, -2, -1));
            blockPos.add(blockPos(user, 1, 2));
            blockPos.add(blockPos(user, -1, 2));
            blockPos.add(blockPos(user, 1, -2));
            blockPos.add(blockPos(user, -1, -2));

            for (BlockPos pos : blockPos) {
                if (world.getBlockState(pos).isAir() && !world.isClientSide()) {
                    world.setBlock(pos, ModBlocks.SPECIAL_SLIME_BLOCK.defaultBlockState(), 6);
                }
            }
            itemStack.consume(1, user);

            for (BlockPos pos : blockPos) {
                if (!world.isClientSide()) {
                    world.getBlockEntity(pos, ModBlockEntityTypes.SLIME_BLOCK_ENTITY)
                            .ifPresent(SlimeBlockEntity::startCountdown);
                }
            }

            playerEntityPvpExtension.AhhPvP$setSelfRescuePlatformCooldown(400);

            return InteractionResult.CONSUME;
        } else {
            if (user instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("This item is in cooldown: " + playerEntityPvpExtension.AhhPvP$getSelfRescuePlatformCooldown() + " ticks").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
            return InteractionResult.FAIL;
        }
    }

    private static BlockPos blockPos(Player user, int i, int j) {
        return new BlockPos(user.blockPosition().getX() + i, user.blockPosition().getY() - 2, user.blockPosition().getZ() + j);
    }
}
