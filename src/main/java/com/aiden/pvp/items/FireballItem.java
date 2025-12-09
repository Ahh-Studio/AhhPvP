package com.aiden.pvp.items;

import com.aiden.pvp.entities.FireballEntity;
import com.aiden.pvp.gamerules.ModGameRules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireballItem extends Item {
    public FireballItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            FireballEntity fireballEntity = new FireballEntity(user, world, itemStack);
            fireballEntity.setItem(itemStack);

            float f = -MathHelper.sin(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            float g = -MathHelper.sin((user.getPitch() + 0.0F) * ((float)Math.PI / 180));
            float h = MathHelper.cos(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            fireballEntity.setVelocity(f, g, h, (float) serverWorld.getGameRules().getInt(ModGameRules.PvpMod_FIREBALL_SHOOT_POWER) / 10, 0.0F);

            if (serverWorld.getGameRules().getBoolean(ModGameRules.PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY)) {
                Vec3d vec3d = user.getMovement();
                fireballEntity.setVelocity(fireballEntity.getVelocity().add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
            }

            world.spawnEntity(fireballEntity);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }
}
