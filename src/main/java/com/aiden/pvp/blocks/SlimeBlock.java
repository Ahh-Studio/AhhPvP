package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SlimeBlock extends HalfTransparentBlock implements EntityBlock {
    public static final MapCodec<SlimeBlock> CODEC = simpleCodec(SlimeBlock::new);

    @Override
    public MapCodec<SlimeBlock> codec() {
        return CODEC;
    }

    public SlimeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (!entity.isSuppressingBounce()) {
            entity.causeFallDamage(fallDistance, 0.0F, world.damageSources().fall());
        }
    }

    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter world, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn(world, entity);
        } else {
            this.bounce(entity);
        }
    }

    private void bounce(Entity entity) {
        Vec3 vec3d = entity.getDeltaMovement();
        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(vec3d.x, -vec3d.y * d, vec3d.z);
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        double d = Math.abs(entity.getDeltaMovement().y);
        if (d < 0.1 && !entity.isSteppingCarefully()) {
            double e = 0.4 + d * 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(e, 1.0, e));
        }

        super.stepOn(world, pos, state, entity);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SlimeBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack)
    {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        // 方块放置时，在服务器端启动计时
        if (!world.isClientSide()) {
            world.getBlockEntity(pos, ModBlockEntityTypes.SLIME_BLOCK_ENTITY)
                    .ifPresent(SlimeBlockEntity::startCountdown);
        }
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : (w, p, s, be) -> SlimeBlockEntity.tick(w, p, s, (SlimeBlockEntity) be);
    }
}
