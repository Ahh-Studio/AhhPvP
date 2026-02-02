package com.aiden.pvp.items;

import com.aiden.pvp.items.component.ModDataComponentTypes;
import com.aiden.pvp.items.component.ReturnScrollComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.Objects;
import java.util.function.Consumer;

public class ReturnScrollItem extends Item {

    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel serverLevel, @NonNull Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
        ReturnScrollComponent component = itemStack.get(ModDataComponentTypes.RETURN_SCROLL);
        if (entity instanceof Player player && component != null) {
            if (component.teleporting() && component.teleportTime() > 0) {
                itemStack.set(ModDataComponentTypes.RETURN_SCROLL, new ReturnScrollComponent(true, component.teleportTime() - 1));
            }
            if (component.teleporting() && component.teleportTime() == 0) {
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
                itemStack.set(ModDataComponentTypes.RETURN_SCROLL, new ReturnScrollComponent(false, 0));
                itemStack.consume(1, player);
            }
        }
    }



    @NonNull
    @Override
    public InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand interactionHand) {
        super.use(level, player, interactionHand);
        ItemStack stack = player.getItemInHand(interactionHand);
        if (stack.get(ModDataComponentTypes.RETURN_SCROLL) != null) {
            boolean bl = Objects.requireNonNull(stack.get(ModDataComponentTypes.RETURN_SCROLL)).teleporting();
            stack.set(ModDataComponentTypes.RETURN_SCROLL, new ReturnScrollComponent(!bl, 100));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(
                Component.literal(Objects.requireNonNull(itemStack.get(ModDataComponentTypes.RETURN_SCROLL)).teleporting()
                ? "Teleport in " + itemStack.get(ModDataComponentTypes.RETURN_SCROLL).teleportTime() + " ticks" : "Use to teleport"
        ));
    }
}
