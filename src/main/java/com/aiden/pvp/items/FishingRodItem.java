package com.aiden.pvp.items;

import com.aiden.pvp.entities.FishingBobberEntity;
import com.aiden.pvp.mixin_extensions.PlayerEntityPvpExtension;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FishingRodItem extends net.minecraft.item.FishingRodItem {
    public FishingRodItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        PlayerEntityPvpExtension userExtension = (PlayerEntityPvpExtension) user;
        ItemStack itemStack = user.getStackInHand(hand);
        if (userExtension.getPvpFishHook() != null) {
            if (!world.isClient()) {
                if (userExtension.getPvpFishHook().shouldDamageStack) itemStack.damage(1, user, hand.getEquipmentSlot());
                userExtension.getPvpFishHook().discard();
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            userExtension.setPvpFishHook(null);
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            //Play Sound
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL,
                    0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            // Spawn Projectile
            if (world instanceof ServerWorld serverWorld) {
                FishingBobberEntity fishingBobberEntity = new FishingBobberEntity(world, user);
                ProjectileEntity.spawn(fishingBobberEntity, serverWorld, itemStack);
                userExtension.setPvpFishHook(fishingBobberEntity);
            }

            //-Item
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START);
        }
        return ActionResult.SUCCESS;
    }
}
