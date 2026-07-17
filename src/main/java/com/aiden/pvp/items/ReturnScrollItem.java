package com.aiden.pvp.items;

import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ReturnScrollItem extends Item {
    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel serverLevel, @NonNull Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
        if (entity instanceof Player player) {
            PlayerEntityPvpExtension playerExt = (PlayerEntityPvpExtension) player;
            if (playerExt.AhhPvP$isTeleportingUsingReturnScroll() && playerExt.AhhPvP$getReturnScrollTeleportCountDown() == 0) {
                playerExt.AhhPvP$setTeleportingUsingReturnScroll(false);
                itemStack.consume(1, player);
            }
        }
    }

    @NonNull
    @Override
    public InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand interactionHand) {
        super.use(level, player, interactionHand);
        PlayerEntityPvpExtension playerExt = (PlayerEntityPvpExtension) player;
        boolean bl = playerExt.AhhPvP$isTeleportingUsingReturnScroll();
        playerExt.AhhPvP$setReturnScrollTeleportCountDown(100);
        playerExt.AhhPvP$setTeleportingUsingReturnScroll(!bl);

        return InteractionResult.SUCCESS;
    }
}
