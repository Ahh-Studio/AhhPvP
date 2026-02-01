package com.aiden.pvp.items;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class FishingRodItem extends net.minecraft.world.item.FishingRodItem {
    public FishingRodItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        super.use(world, user, hand);
        PlayerEntityPvpExtension userExtension = (PlayerEntityPvpExtension) user;
        ItemStack itemStack = user.getItemInHand(hand);
        if (userExtension.getPvpFishHook() != null) {
            if (!world.isClientSide()) {
                if (userExtension.getPvpFishHook().shouldDamageStack) itemStack.hurtAndBreak(1, user, hand.asEquipmentSlot());
                userExtension.getPvpFishHook().discard();
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            userExtension.setPvpFishHook(null);
            user.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            //Play Sound
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
                    0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            // Spawn Projectile
            if (world instanceof ServerLevel serverWorld) {
                FishingBobberEntity fishingBobberEntity = new FishingBobberEntity(world, user);
                Projectile.spawnProjectile(fishingBobberEntity, serverWorld, itemStack);
                userExtension.setPvpFishHook(fishingBobberEntity);
            }

            //-Item
            user.awardStat(Stats.ITEM_USED.get(this));
            user.gameEvent(GameEvent.ITEM_INTERACT_START);
        }
        return InteractionResult.SUCCESS;
    }
}
