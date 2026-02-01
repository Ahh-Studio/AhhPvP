package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static com.aiden.pvp.entities.ModEntityTypes.BED_BUG;

public class BedBugEntity extends ThrowableItemProjectile {
    public BedBugEntity(EntityType<? extends BedBugEntity> entityType, Level world) {
        super(entityType, world);
    }

    public BedBugEntity(double x, double y, double z, Level world, ItemStack stack) {
        super(BED_BUG, x, y, z, world, stack);
    }

    public BedBugEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(BED_BUG, owner, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BED_BUG;
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.DEATH) {
            ParticleOptions particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.spawnBug(
                this.level(),
                this.getX() - (this.getDeltaMovement().x * 0.5),
                this.getY() - (this.getDeltaMovement().y * 0.5),
                this.getZ() - (this.getDeltaMovement().z * 0.5)
        );
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        this.spawnBug(
                this.level(),
                this.getX(),
                this.getY(),
                this.getZ()
        );
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
            this.discard();
        }
    }

    private void spawnBug(Level world, double x, double y, double z) {
        Silverfish silverfishEntity = EntityType.SILVERFISH.create(world, EntitySpawnReason.TRIGGERED);
        if (silverfishEntity != null) {
            silverfishEntity.snapTo(x, y, z, world.getRandom().nextFloat() * 360.0F, 0.0F);
            world.addFreshEntity(silverfishEntity);
            silverfishEntity.playSound(SoundEvents.SILVERFISH_HURT);
        }
    }

    private ParticleOptions getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemStack));
    }
}
