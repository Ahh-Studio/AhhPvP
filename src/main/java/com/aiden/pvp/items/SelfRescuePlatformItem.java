package com.aiden.pvp.items;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class SelfRescuePlatformItem extends Item {
    public SelfRescuePlatformItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        ArrayList<BlockPos> blockPos = new ArrayList<BlockPos>();
        blockPos.add(blockPos(user, 0, -1, 0));
        blockPos.add(blockPos(user, 1, -1, 0));
        blockPos.add(blockPos(user, -1, -1, 0));
        blockPos.add(blockPos(user, 0, -1, 1));
        blockPos.add(blockPos(user, 1, -1, 1));
        blockPos.add(blockPos(user, -1, -1, 1));
        blockPos.add(blockPos(user, 0, -1, -1));
        blockPos.add(blockPos(user, 1, -1, -1));
        blockPos.add(blockPos(user, -1, -1, -1));
        blockPos.add(blockPos(user, 2, -1, 1));
        blockPos.add(blockPos(user, 2, -1, -1));
        blockPos.add(blockPos(user, -2, -1, 1));
        blockPos.add(blockPos(user, -2, -1, -1));
        blockPos.add(blockPos(user, 1, -1, 2));
        blockPos.add(blockPos(user, -1, -1, 2));
        blockPos.add(blockPos(user, 1, -1, -2));
        blockPos.add(blockPos(user, -1, -1, -2));

        for (BlockPos pos : blockPos) {
            if (
                    world.getBlockState(pos).is(Blocks.AIR)
                            ||
                    world.getBlockState(pos).is(Blocks.CAVE_AIR)
            ) {
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

        return InteractionResult.SUCCESS;
    }
    private static BlockPos blockPos(Player user, int i, int j, int k) {
        return new BlockPos(user.blockPosition().getX()+i, user.blockPosition().getY()+j, user.blockPosition().getZ()+k);
    }
}
