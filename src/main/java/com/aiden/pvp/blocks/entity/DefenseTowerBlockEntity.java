package com.aiden.pvp.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DefenseTowerBlockEntity extends ChestBlockEntity {
    public DefenseTowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY, pos, state);
    }
}
