package com.aiden.pvp.items;

import com.aiden.pvp.entities.ChickenDefenseEntity;
import com.aiden.pvp.entities.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ChickenDefenseItem extends Item {
    public ChickenDefenseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();

        ItemStack itemStack = useOnContext.getItemInHand();
        BlockPos blockPos = useOnContext.getClickedPos();
        Direction direction = useOnContext.getClickedFace();
        BlockState blockState = level.getBlockState(blockPos);

        if (direction != Direction.UP) {
            return InteractionResult.PASS;
        }

        BlockPos blockPos2;
        if (blockState.getCollisionShape(level, blockPos).isEmpty()) {
            blockPos2 = blockPos;
        } else {
            blockPos2 = blockPos.relative(direction);
        }

        if (level instanceof ServerLevel serverLevel) {
            return this.spawnEntity(useOnContext.getPlayer(), itemStack, serverLevel, blockPos2, !blockPos.equals(blockPos2));
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult spawnEntity(LivingEntity livingEntity, ItemStack itemStack, ServerLevel level, BlockPos blockPos, boolean bl2) {
        ChickenDefenseEntity chicken = ModEntityTypes.CHICKEN_DEFENSE.spawn(level, itemStack, livingEntity, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, true, bl2);
        if (chicken != null) {
            itemStack.consume(1, livingEntity);
            level.gameEvent(livingEntity, GameEvent.ENTITY_PLACE, blockPos);
        }
        return InteractionResult.SUCCESS;
    }
}
