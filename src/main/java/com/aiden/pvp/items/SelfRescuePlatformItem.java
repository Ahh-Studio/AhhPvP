package com.aiden.pvp.items;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import java.util.ArrayList;

import com.aiden.pvp.items.component.ModDataComponentTypes;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public class SelfRescuePlatformItem extends Item {
    public SelfRescuePlatformItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        PlayerEntityPvpExtension playerEntityPvpExtension = (PlayerEntityPvpExtension) user;
        if (playerEntityPvpExtension.getSelfRescuePlatformCooldown() <= 0) {
            ItemStack itemStack = user.getItemInHand(hand);

            ArrayList<BlockPos> blockPos = new ArrayList<>();
            blockPos.add(blockPos(user, 0, -1, 0));
            blockPos.add(blockPos(user, 1, -1, 0));
            blockPos.add(blockPos(user, -1, -1, 0));
            blockPos.add(blockPos(user, 0, -1, 1));
            blockPos.add(blockPos(user, 1, -1, 1));
            blockPos.add(blockPos(user, -1, -1, 1));
            blockPos.add(blockPos(user, 0, -1, -1));
            blockPos.add(blockPos(user, 1, -1, -1));
            blockPos.add(blockPos(user, -1, -1, -1));
            blockPos.add(blockPos(user, 2, -1, 1));
            blockPos.add(blockPos(user, 2, -1, -1));
            blockPos.add(blockPos(user, -2, -1, 1));
            blockPos.add(blockPos(user, -2, -1, -1));
            blockPos.add(blockPos(user, 1, -1, 2));
            blockPos.add(blockPos(user, -1, -1, 2));
            blockPos.add(blockPos(user, 1, -1, -2));
            blockPos.add(blockPos(user, -1, -1, -2));

            for (BlockPos pos : blockPos) {
                if (world.getBlockState(pos).isAir() && !world.isClientSide()) {
                    world.setBlock(pos, ModBlocks.SPECIAL_SLIME_BLOCK.defaultBlockState(), 6);
                }
            }
            itemStack.consume(1, user);

            for (BlockPos pos : blockPos) {
                if (!world.isClientSide()) {
                    world.getBlockEntity(pos, ModBlockEntityTypes.SLIME_BLOCK_ENTITY)
                            .ifPresent(SlimeBlockEntity::startCountdown);
                }
            }

            playerEntityPvpExtension.setSelfRescuePlatformCooldown(400);
            return InteractionResult.SUCCESS;
        } else {
            if (user instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("This item is in cooldown: " + playerEntityPvpExtension.getSelfRescuePlatformCooldown() + " ticks"));
            }
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);

        if (entity instanceof Player player) {
            PlayerEntityPvpExtension playerEntityPvpExtension = (PlayerEntityPvpExtension) player;
            if (playerEntityPvpExtension.getSelfRescuePlatformCooldown() > 0) {
                playerEntityPvpExtension.setSelfRescuePlatformCooldown(playerEntityPvpExtension.getSelfRescuePlatformCooldown() - 1);
            }
        }
    }

    private static BlockPos blockPos(Player user, int i, int j, int k) {
        return new BlockPos(user.blockPosition().getX() + i, user.blockPosition().getY() + j, user.blockPosition().getZ() + k);
    }
}
