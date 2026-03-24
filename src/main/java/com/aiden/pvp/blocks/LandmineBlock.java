package com.aiden.pvp.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;

public class LandmineBlock extends Block {
    public static final VoxelShape SHAPE = Shapes.or(Block.column(10.0, 0.0, 3.0), Block.column(8.0, 3.0, 4.0));

    public LandmineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState blockState) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
        this.explode(level, blockPos);
    }

    private void explode(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            level.explode(
                    null,
                    Explosion.getDefaultDamageSource(level, null),
                    null,
                    pos.getCenter().x(),
                    pos.getCenter().y(),
                    pos.getCenter().z(),
                    4.0F,
                    false,
                    Level.ExplosionInteraction.TNT
            );
        }
    }
}
