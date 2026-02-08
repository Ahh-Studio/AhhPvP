package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;

public class EgglletEntity extends ThrowableItemProjectile {
    public int age = 200;

    public EgglletEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public EgglletEntity(EntityType<? extends ThrowableItemProjectile> entityType, double d, double e, double f, Level level, ItemStack itemStack) {
        super(ModEntityTypes.EGGLLIT, d, e, f, level, itemStack);
    }

    public EgglletEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity livingEntity, Level level, ItemStack itemStack) {
        super(ModEntityTypes.EGGLLIT, livingEntity, level, itemStack);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > 0) this.age--;
        if (this.age == 0) this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            entityHitResult.getEntity().hurtServer(serverLevel, this.damageSources().thrown(this, this.getOwner()), 5.0F);
        }
    }

    @Override
    protected void onHit(@NonNull HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.random.nextInt(200) == 0) {
                ChickenDefenseEntity chickenDefenseEntity = ModEntityTypes.CHICKEN_DEFENSE.spawn(
                        serverLevel,
                        new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z),
                        EntitySpawnReason.TRIGGERED
                );
            }

            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return ModItems.EGGLLET;
    }

    @Override
    protected void applyGravity() {
    }
}
