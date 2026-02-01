package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.BossBattleHandlerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BossBattleHandlerBlock extends BaseEntityBlock {

    protected BossBattleHandlerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BossBattleHandlerBlock::new);
    }

    @Override
    public @Nullable BossBattleHandlerBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BossBattleHandlerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (world.isClientSide()) {
            return null;
        } else {
            return (w, p, s, be) -> {
                if (w.getBlockEntity(p) instanceof BossBattleHandlerBlockEntity bossBattleHandlerBlockEntity) bossBattleHandlerBlockEntity.tick(w, p, s, be);
            };
        }
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof BossBattleHandlerBlockEntity bossBattleHandlerBlockEntity) {
            bossBattleHandlerBlockEntity.removeBossBarPlayers();
        }
        super.playerDestroy(world, player, pos, state, blockEntity, tool);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof BossBattleHandlerBlockEntity bossBattleHandlerBlockEntity) {
            bossBattleHandlerBlockEntity.removeBossBarPlayers();
        }
        return super.playerWillDestroy(world, pos, state, player);
    }
}
