package com.aiden.pvp.items;

import com.aiden.pvp.entities.DaggerEntity;
import com.aiden.pvp.entities.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ThrowableDaggerItem extends Item {
    public ThrowableDaggerItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        super.use(world, user, hand);
        if (world instanceof ServerLevel serverWorld) {
            DaggerEntity daggerEntity = new DaggerEntity(ModEntityTypes.DAGGER, world);
            daggerEntity.setOwner(user);

            daggerEntity.setPosRaw(user.getX(), user.getEyeY(), user.getZ());

            float f = -Mth.sin(user.getYRot() * ((float)Math.PI / 180)) * Mth.cos(user.getXRot() * ((float)Math.PI / 180));
            float g = -Mth.sin((user.getXRot() + 0.0F) * ((float)Math.PI / 180));
            float h = Mth.cos(user.getYRot() * ((float)Math.PI / 180)) * Mth.cos(user.getXRot() * ((float)Math.PI / 180));
            daggerEntity.shoot(f, g, h, 1.2F, 0.0F);

            serverWorld.addFreshEntity(daggerEntity);
        }

        return InteractionResult.SUCCESS;
    }
}
