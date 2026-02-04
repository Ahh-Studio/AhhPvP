package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.blocks.SlimeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SlimeBlockEntity extends BlockEntity {
    public SlimeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SLIME_BLOCK_ENTITY, pos, state);
    }

    public void startCountdown() {
        this.getBlockState().setValue(SlimeBlock.VANISH_COUNTDOWN, 400);
        this.setChanged();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, SlimeBlockEntity entity) {
        if (world.isClientSide()) return;

        int i = state.getValue(SlimeBlock.VANISH_COUNTDOWN);

        if (i > 0) {
            BlockState newState = state.setValue(SlimeBlock.VANISH_COUNTDOWN, i - 1);
            world.setBlock(pos, newState, 3);
            entity.setChanged();
        } else {
            world.removeBlock(pos, false);
            entity.setChanged();
        }
    }
}
