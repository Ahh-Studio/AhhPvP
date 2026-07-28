package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.DefenseTowerBlockEntity;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DefenseTowerBlock extends ChestBlock {
    public DefenseTowerBlock(Properties properties) {
        super(() -> ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DefenseTowerBlockEntity(pos, state);
    }
}
