package com.aiden.pvp.items;

import com.aiden.pvp.entities.BridgeEggEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BridgeEggItem extends EggItem {
    private static final float THROW_POWER = 1.2F;
    public BridgeEggItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        world.playSound(
                null,
                user.getX(), user.getY(), user.getZ(),
                SoundEvents.EGG_THROW,
                SoundSource.PLAYERS
        );

        if (world instanceof ServerLevel serverWorld) {
            Projectile.spawnProjectile(
                    new BridgeEggEntity(serverWorld, user, itemStack),
                    serverWorld,
                    itemStack,
                    projectile -> {
                        float xd = -Mth.sin(user.getYRot() * (float) (Math.PI / 180.0)) * Mth.cos(user.getXRot() * (float) (Math.PI / 180.0));
                        float yd = -Mth.sin((user.getXRot() + 0.0F) * (float) (Math.PI / 180.0));
                        float zd = Mth.cos(user.getYRot() * (float) (Math.PI / 180.0)) * Mth.cos(user.getXRot() * (float) (Math.PI / 180.0));
                        projectile.shoot(xd, yd, zd, THROW_POWER, 1.0F);
                    }
            );
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);

        return InteractionResult.SUCCESS;
    }
}
