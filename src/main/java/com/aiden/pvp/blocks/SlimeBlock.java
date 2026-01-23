package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlimeBlock extends TranslucentBlock implements BlockEntityProvider {
    public static final MapCodec<SlimeBlock> CODEC = createCodec(SlimeBlock::new);

    @Override
    public MapCodec<SlimeBlock> getCodec() {
        return CODEC;
    }

    public SlimeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (!entity.bypassesLandingEffects()) {
            entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            this.bounce(entity);
        }
    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        double d = Math.abs(entity.getVelocity().y);
        if (d < 0.1 && !entity.bypassesSteppingEffects()) {
            double e = 0.4 + d * 0.2;
            entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlimeBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(
            World world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        // 方块放置时，在服务器端启动计时
        if (!world.isClient()) {
            world.getBlockEntity(pos, ModBlockEntityTypes.SLIME_BLOCK_ENTITY)
                    .ifPresent(SlimeBlockEntity::startCountdown);
        }
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : (w, p, s, be) -> SlimeBlockEntity.tick(w, p, s, (SlimeBlockEntity) be);
    }
}
