package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import static com.aiden.pvp.entities.ModEntityTypes.BED_BUG;

public class BedBugEntity extends ThrownItemEntity {
    public BedBugEntity(EntityType<? extends BedBugEntity> entityType, World world) {
        super(entityType, world);
    }

    public BedBugEntity(double x, double y, double z, World world, ItemStack stack) {
        super(BED_BUG, x, y, z, world, stack);
    }

    public BedBugEntity(World world, LivingEntity owner, ItemStack stack) {
        super(BED_BUG, owner, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BED_BUG;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; i++) {
                this.getEntityWorld().addParticleClient(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.spawnBug(
                this.getEntityWorld(),
                this.getX() - (this.getVelocity().x * 0.5),
                this.getY() - (this.getVelocity().y * 0.5),
                this.getZ() - (this.getVelocity().z * 0.5)
        );
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        this.spawnBug(
                this.getEntityWorld(),
                this.getX(),
                this.getY(),
                this.getZ()
        );
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getEntityWorld().isClient()) {
            this.getEntityWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    private void spawnBug(World world, double x, double y, double z) {
        SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world, SpawnReason.TRIGGERED);
        if (silverfishEntity != null) {
            silverfishEntity.refreshPositionAndAngles(x, y, z, world.getRandom().nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(silverfishEntity);
            silverfishEntity.playSoundIfNotSilent(SoundEvents.ENTITY_SILVERFISH_HURT);
        }
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }
}
