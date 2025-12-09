package com.aiden.pvp.items;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class SelfRescuePlatformItem extends Item {
    public SelfRescuePlatformItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

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
                    world.getBlockState(pos).isOf(Blocks.AIR)
                            ||
                    world.getBlockState(pos).isOf(Blocks.CAVE_AIR)
            ) {
                world.setBlockState(pos, ModBlocks.SPECIAL_SLIME_BLOCK.getDefaultState(), 6);
            }
        }
        itemStack.decrementUnlessCreative(1, user);

        for (BlockPos pos : blockPos) {
            if (!world.isClient()) {
                world.getBlockEntity(pos, ModBlockEntityTypes.SLIME_BLOCK_ENTITY)
                        .ifPresent(SlimeBlockEntity::startCountdown);
            }
        }

        return ActionResult.SUCCESS;
    }
    private static BlockPos blockPos(PlayerEntity user, int i, int j, int k) {
        return new BlockPos(user.getBlockPos().getX()+i, user.getBlockPos().getY()+j, user.getBlockPos().getZ()+k);
    }
}
