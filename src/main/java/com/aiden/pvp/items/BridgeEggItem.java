package com.aiden.pvp.items;

import com.aiden.pvp.entities.BridgeEggEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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

        // 播放投掷声音（客户端和服务器都能听到）
        world.playSound(
                null,
                user.getX(), user.getY(), user.getZ(),
                SoundEvents.EGG_THROW,
                SoundSource.PLAYERS
        );

        // 仅在服务器端生成实体（确保同步到客户端）
        if (world instanceof ServerLevel serverWorld) {
            // 使用定义的投掷力度，替换原POWER
            Projectile.spawnProjectileFromRotation(
                    BridgeEggEntity::new,  // 实体构造器
                    serverWorld,          // 服务器世界
                    itemStack,            // 物品栈
                    user,                 // 投掷者
                    0.0F,                 // 垂直偏移
                    THROW_POWER,          // 投掷力度
                    1.0F                  // 散布范围（1.0F为原版鸡蛋散布）
            );
        }

        // 更新统计信息和物品数量
        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);

        return InteractionResult.SUCCESS;
    }
}
