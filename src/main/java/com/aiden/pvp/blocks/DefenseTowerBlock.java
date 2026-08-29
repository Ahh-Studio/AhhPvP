package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.DefenseTowerBlockEntity;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;

public class DefenseTowerBlock extends ChestBlock {
    private static final Map<ResourceKey<Level>, List<PendingTowerTask>> pendingTasksByDimension = new HashMap<>();

    public DefenseTowerBlock(Properties properties) {
        super(() -> ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && oldState.getBlock() != this) {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            List<TaskEntry> entries = new ArrayList<>();

            entries.add(new TaskEntry(pos, Blocks.AIR.defaultBlockState()));
            for (int i = 0; i < 4; i++) {
                BlockPos pos1 = pos.above(i);
                entries.add(new TaskEntry(pos1, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir)));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()).relative(dir.getClockWise()), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()).relative(dir.getCounterClockWise()), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()).relative(dir.getClockWise()).relative(dir.getClockWise()).relative(dir), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()).relative(dir.getCounterClockWise()).relative(dir.getCounterClockWise()).relative(dir), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()).relative(dir.getClockWise()).relative(dir.getClockWise()).relative(dir).relative(dir), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir.getOpposite()).relative(dir.getCounterClockWise()).relative(dir.getCounterClockWise()).relative(dir).relative(dir), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir).relative(dir).relative(dir.getClockWise()), Blocks.SANDSTONE.defaultBlockState()));
                entries.add(new TaskEntry(pos1.relative(dir).relative(dir).relative(dir.getCounterClockWise()), Blocks.SANDSTONE.defaultBlockState()));
            }

            pendingTasksByDimension.computeIfAbsent(level.dimension(), k -> new ArrayList<>()).add(new PendingTowerTask(pos, level.dimension(), entries));
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DefenseTowerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == ModBlockEntityTypes.DEFENSE_TOWER_BLOCK_ENTITY && !level.isClientSide()
                ? (level1, pos, state1, blockEntity) -> DefenseTowerBlockEntity.tick(level1, pos, state1, (DefenseTowerBlockEntity) blockEntity)
                : null;
    }

    public static boolean hasPendingTasks() {
        return !pendingTasksByDimension.isEmpty();
    }

    public static void processTasks(Level level) {
        ResourceKey<Level> currentDim = level.dimension();
        List<PendingTowerTask> tasks = pendingTasksByDimension.get(currentDim);
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        Iterator<PendingTowerTask> it = tasks.iterator();
        while (it.hasNext()) {
            PendingTowerTask task = it.next();
            TaskEntry next = task.next();
            if (next != null) {
                BlockPos target = next.pos();
                BlockState state = next.state();
                if (state.is(Blocks.AIR) || state.is(Blocks.LADDER) || level.getBlockState(target).isAir()) {
                    level.setBlock(target, state, 3);
                }
            }
            if (task.isDone()) {
                it.remove();
            }
        }
        if (tasks.isEmpty()) {
            pendingTasksByDimension.remove(currentDim);
        }
    }

    private static class PendingTowerTask {
        private final BlockPos origin;
        private final ResourceKey<Level> levelKey;
        private final List<TaskEntry> entries;
        private int index;

        PendingTowerTask(BlockPos origin, ResourceKey<Level> levelKey, List<TaskEntry> entries) {
            this.origin = origin;
            this.levelKey = levelKey;
            this.entries = entries;
            this.index = 0;
        }

        TaskEntry next() {
            if (index >= entries.size()) {
                return null;
            }
            return entries.get(index++);
        }

        boolean isDone() {
            return index >= entries.size();
        }

        public BlockPos getOrigin() {
            return origin;
        }
    }

    private record TaskEntry(BlockPos pos, BlockState state) {
    }
}
