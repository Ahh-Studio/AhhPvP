package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.BossSpawnerBlockEntity;
import com.aiden.pvp.entities.ModEntityTypes;
import com.aiden.pvp.items.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class BossSpawnerBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 8.0);

    public BossSpawnerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BossSpawnerBlock::new);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(ModItems.BOSS_KEY) && world instanceof ServerLevel serverWorld) {
            if (serverWorld.getBlockState(pos.below()).is(Blocks.TNT) &&
                    serverWorld.getBlockState(pos.below().east().north()).is(Blocks.TNT) &&
                    serverWorld.getBlockState(pos.below().east().south()).is(Blocks.TNT) &&
                    serverWorld.getBlockState(pos.below().west().north()).is(Blocks.TNT) &&
                    serverWorld.getBlockState(pos.below().west().south()).is(Blocks.TNT) &&
                    serverWorld.getBlockState(pos.below().east()).is(ModBlocks.STRONG_GLASS) &&
                    serverWorld.getBlockState(pos.below().west()).is(ModBlocks.STRONG_GLASS) &&
                    serverWorld.getBlockState(pos.below().north()).is(ModBlocks.STRONG_GLASS) &&
                    serverWorld.getBlockState(pos.below().south()).is(ModBlocks.STRONG_GLASS)) {
                if (world.getBlockEntity(pos) instanceof BossSpawnerBlockEntity blockEntity) {
                    this.summonMurderer(world, pos.above());
                    blockEntity.placeStructure(serverWorld);
                }
            }
            stack.consume(1, player);
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected @NonNull VoxelShape getCollisionShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NonNull VoxelShape getOcclusionShape(@NonNull BlockState state) {
        return SHAPE;
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    private void summonMurderer(Level world, Vec3i pos) {
        if (world instanceof ServerLevel serverWorld) {
            ModEntityTypes.MURDERER.spawn(serverWorld, new BlockPos(pos), EntitySpawnReason.EVENT);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BossSpawnerBlockEntity(pos, state);
    }
}
