package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.blocks.SlimeBlock;
import com.aiden.pvp.gamerules.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SlimeBlockEntity extends BlockEntity {
    public SlimeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SLIME_BLOCK_ENTITY, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, SlimeBlockEntity entity) {
        if (world.isClientSide()) return;

        int i = state.getValue(SlimeBlock.VANISH_COUNTDOWN);

        if (i <= 0) {
            if (world instanceof ServerLevel serverLevel) {
                int vanishCountdown = serverLevel.getGameRules().get(ModGameRules.SELF_RES_PLATFORM_DISAPPEAR_TIME);
                world.setBlock(pos, state.setValue(SlimeBlock.VANISH_COUNTDOWN, vanishCountdown), 6);
            }
        } else if (i > 1) {
            world.setBlock(pos, state.setValue(SlimeBlock.VANISH_COUNTDOWN, i - 1), 6);
        } else {
            world.removeBlock(pos, false);
        }
    }
}
