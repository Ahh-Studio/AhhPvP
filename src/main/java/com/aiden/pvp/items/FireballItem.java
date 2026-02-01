package com.aiden.pvp.items;

import com.aiden.pvp.entities.FireballEntity;
import com.aiden.pvp.gamerules.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FireballItem extends Item implements ProjectileItem {
    public FireballItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (world instanceof ServerLevel serverWorld) {
            FireballEntity fireballEntity = new FireballEntity(user, world, itemStack);
            fireballEntity.setItem(itemStack);

            float f = -Mth.sin(user.getYRot() * ((float)Math.PI / 180)) * Mth.cos(user.getXRot() * ((float)Math.PI / 180));
            float g = -Mth.sin((user.getXRot() + 0.0F) * ((float)Math.PI / 180));
            float h = Mth.cos(user.getYRot() * ((float)Math.PI / 180)) * Mth.cos(user.getXRot() * ((float)Math.PI / 180));
            fireballEntity.shoot(f, g, h, (float) serverWorld.getGameRules().get(ModGameRules.PvpMod_FIREBALL_SHOOT_POWER) / 10, 0.0F);

            if (serverWorld.getGameRules().get(ModGameRules.PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY)) {
                Vec3 vec3d = user.getKnownMovement();
                fireballEntity.setDeltaMovement(fireballEntity.getDeltaMovement().add(vec3d.x, user.onGround() ? 0.0 : vec3d.y, vec3d.z));
            }

            world.addFreshEntity(fireballEntity);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        return new FireballEntity(world, pos.x(), pos.y(), pos.z(), stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player user = context.getPlayer();
        InteractionHand hand = context.getHand();
        if (user == null) return InteractionResult.PASS;

        // shoot
        ItemStack itemStack = user.getItemInHand(hand);
        if (world instanceof ServerLevel serverWorld) {
            FireballEntity fireballEntity = new FireballEntity(user, world, itemStack);
            fireballEntity.setItem(itemStack);

            float f = -Mth.sin(user.getYRot() * ((float)Math.PI / 180)) * Mth.cos(user.getXRot() * ((float)Math.PI / 180));
            float g = -Mth.sin((user.getXRot() + 0.0F) * ((float)Math.PI / 180));
            float h = Mth.cos(user.getYRot() * ((float)Math.PI / 180)) * Mth.cos(user.getXRot() * ((float)Math.PI / 180));
            fireballEntity.shoot(f, g, h, (float) serverWorld.getGameRules().get(ModGameRules.PvpMod_FIREBALL_SHOOT_POWER) / 10, 0.0F);

            if (serverWorld.getGameRules().get(ModGameRules.PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY)) {
                Vec3 vec3d = user.getKnownMovement();
                fireballEntity.setDeltaMovement(fireballEntity.getDeltaMovement().add(vec3d.x, user.onGround() ? 0.0 : vec3d.y, vec3d.z));
            }

            world.addFreshEntity(fireballEntity);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);

        // summon fire block
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!CampfireBlock.canLight(blockState) && !CandleBlock.canLight(blockState) && !CandleCakeBlock.canLight(blockState)) {
            blockPos = blockPos.relative(context.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(world, blockPos, context.getHorizontalDirection())) {
                world.setBlockAndUpdate(blockPos, BaseFireBlock.getState(world, blockPos));
                world.gameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockPos);
            }
        } else {
            world.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, true));
            world.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
        }

        return InteractionResult.SUCCESS;
    }
}
