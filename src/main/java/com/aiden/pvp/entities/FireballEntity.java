package com.aiden.pvp.entities;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.util.explosion.FireballExplosionImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FireballEntity extends ThrowableItemProjectile {
    private float explosionPower = 12.0F;
    private final float explosionDamage = 2.0F;

    public FireballEntity(LivingEntity owner, Level world, ItemStack stack) {
        super(ModEntityTypes.FIREBALL, owner, world, stack);
    }

    public FireballEntity(EntityType<? extends FireballEntity> entityType, Level world) {
        super(entityType, world);
    }

    public FireballEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntityTypes.FIREBALL, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FIREBALL;
    }

    @Override
    public void tick() {
        Vec3 velocity = this.getDeltaMovement();
        super.tick();
        this.setDeltaMovement(velocity);

        this.setSharedFlagOnFire(true);

        if (this.tickCount > 400) this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Level var3 = this.level();
        if (var3 instanceof ServerLevel serverWorld) {
            explosionPower = (float) serverWorld.getGameRules().get(ModGameRules.FIREBALL_EXPLODE_POWER) / 10;
            this.explode(this.explosionPower);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level() instanceof ServerLevel serverWorld) {
            explosionPower = (float) serverWorld.getGameRules().get(ModGameRules.FIREBALL_EXPLODE_POWER) / 10;
            this.explode(this.explosionPower);
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.hurtServer(serverWorld, damageSources().explosion(this, this.getOwner()), 2.0F);
            }
        }
        this.discard();
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
    }

    @Override
    protected void applyGravity() {
    }

    private void explode(float power) {
        if (level() instanceof ServerLevel serverWorld) {
            boolean createFire = serverWorld.getGameRules().get(ModGameRules.FIREBALL_CREATES_FIRE);
            FireballExplosionImpl.createExplosion(
                    this.level(), this,
                    serverWorld.damageSources().explosion(this, this.getOwner()),
                    new SimpleExplosionDamageCalculator(
                            true, true,
                            Optional.of(explosionDamage), Optional.empty()
                    ),
                    this.getX() + 0.0, this.getY() + 0.0, this.getZ() + 0.0,
                    power, createFire, Level.ExplosionInteraction.MOB, SoundEvents.GENERIC_EXPLODE
            );
        }
    }
}
