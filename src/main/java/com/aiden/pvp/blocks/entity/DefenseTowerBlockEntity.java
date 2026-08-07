package com.aiden.pvp.blocks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class DefenseTowerBlockEntity extends ChestBlockEntity {
    private static final Codec<List<BlockPos>> POSITIONS_CODEC = BlockPos.CODEC.listOf();

    private List<BlockPos> pendingSandstonePositions;

    public DefenseTowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY, pos, state);
    }

    public void setPendingSandstonePositions(List<BlockPos> positions) {
        this.pendingSandstonePositions = new ArrayList<>(positions);
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (pendingSandstonePositions != null) {
            output.store("PendingSandstonePositions", POSITIONS_CODEC, pendingSandstonePositions);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        pendingSandstonePositions = input.listOrEmpty("PendingSandstonePositions", BlockPos.CODEC)
                .stream().collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        if (pendingSandstonePositions.isEmpty()) {
            pendingSandstonePositions = null;
        }
    }

    public static void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, DefenseTowerBlockEntity blockEntity) {
        if (blockEntity.pendingSandstonePositions == null || blockEntity.pendingSandstonePositions.isEmpty()) {
            return;
        }
        BlockPos target = blockEntity.pendingSandstonePositions.remove(0);
        if (level.getBlockState(target).isAir()) {
            level.setBlock(target, Blocks.SANDSTONE.defaultBlockState(), 3);
        }
        if (blockEntity.pendingSandstonePositions.isEmpty()) {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            level.setBlock(pos, Blocks.LADDER.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, dir), 3);
        } else {
            blockEntity.setChanged();
        }
    }
}
