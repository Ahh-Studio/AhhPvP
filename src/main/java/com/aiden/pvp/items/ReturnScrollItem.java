package com.aiden.pvp.items;

import com.aiden.pvp.items.component.ModDataComponentTypes;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ReturnScrollItem extends Item {
    private Entity entity;

    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel serverLevel, @NonNull Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
        Boolean isTeleporting = itemStack.get(ModDataComponentTypes.IS_TELEPORTING);
        this.entity = entity;
        if (entity instanceof Player player && isTeleporting != null) {
            PlayerEntityPvpExtension playerEntityPvpExtension = (PlayerEntityPvpExtension) player;
            if (isTeleporting && playerEntityPvpExtension.AhhPvP$getReturnScrollTeleportCountDown() > 0) {
                if (player.getKnownMovement().horizontalDistanceSqr() + player.getKnownMovement().y() * player.getKnownMovement().y() == 0) { // 模长
                    playerEntityPvpExtension.AhhPvP$setReturnScrollTeleportCountDown(playerEntityPvpExtension.AhhPvP$getReturnScrollTeleportCountDown() - 1);
                } else {
                    playerEntityPvpExtension.AhhPvP$setReturnScrollTeleportCountDown(100);
                    itemStack.set(ModDataComponentTypes.IS_TELEPORTING, false);
                }
            }
            if (isTeleporting && playerEntityPvpExtension.AhhPvP$getReturnScrollTeleportCountDown() == 0) {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BLACK_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BLUE_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.BROWN_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.CYAN_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.GRAY_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.GREEN_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.LIGHT_BLUE_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.LIME_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.LIGHT_GRAY_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.MAGENTA_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.ORANGE_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.PINK_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.PURPLE_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.RED_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.WHITE_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.YELLOW_BED)
                            || serverLevel.getBlockState(serverPlayer.getRespawnConfig().respawnData().pos()).is(Blocks.RESPAWN_ANCHOR)
                    ) {
                        serverPlayer.teleportTo(
                                serverPlayer.getRespawnConfig().respawnData().pos().getCenter().x,
                                serverPlayer.getRespawnConfig().respawnData().pos().getCenter().y + 0.5,
                                serverPlayer.getRespawnConfig().respawnData().pos().getCenter().z);
                    } else {
                        serverPlayer.teleportTo(
                                serverLevel.getRespawnData().pos().getX(),
                                serverLevel.getRespawnData().pos().getY() + 0.5,
                                serverLevel.getRespawnData().pos().getZ()
                        );
                    }
                }
                itemStack.set(ModDataComponentTypes.IS_TELEPORTING, false);
                itemStack.consume(1, player);
            }
        }
    }



    @NonNull
    @Override
    public InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand interactionHand) {
        super.use(level, player, interactionHand);
        ItemStack stack = player.getItemInHand(interactionHand);
        PlayerEntityPvpExtension playerPvpExtension = (PlayerEntityPvpExtension) player;
        if (stack.get(ModDataComponentTypes.IS_TELEPORTING) != null) {
            boolean bl = Boolean.TRUE.equals(stack.get(ModDataComponentTypes.IS_TELEPORTING));
            playerPvpExtension.AhhPvP$setReturnScrollTeleportCountDown(100);
            stack.set(ModDataComponentTypes.IS_TELEPORTING, !bl);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack itemStack) {
        if (this.entity instanceof Player player) {
            PlayerEntityPvpExtension playerPvpExtension = (PlayerEntityPvpExtension) player;
            return Component.literal(super.getName(itemStack).getString() +
                    (Boolean.TRUE.equals(itemStack.get(ModDataComponentTypes.IS_TELEPORTING)) ?
                    " (Teleport in " + playerPvpExtension.AhhPvP$getReturnScrollTeleportCountDown() + " ticks)" : "")
            );
        } else return super.getName(itemStack);
    }
}
