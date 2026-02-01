package com.aiden.pvp.entities;

import com.aiden.pvp.explosion.FireballExplosionImpl;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import java.util.Optional;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireballEntity extends ThrowableItemProjectile {
    private float explosionPower = 12.0F;
    private final float explosionDamage = 2.0F;

    public FireballEntity(double x, double y, double z, Level world, ItemStack stack) {
        super(ModEntityTypes.FIREBALL, x, y, z, world, stack);
    }

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
            explosionPower = (float) serverWorld.getGameRules().get(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER) / 10;
            // fire
            this.explode(
                    0.5F, true, false, true, 0F, 1.0F,
                    WeightedList.<ExplosionParticleInfo>builder().build(), 0, 0, 0
            );
            // particle
            this.explode(
                    12F, false, false, false, 0F, 0F,
                    WeightedList.<ExplosionParticleInfo>builder()
                            .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
                            .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
                            .build(), 0, 0, 0
            );
            // kb and damage
            this.explode(
                    this.explosionPower, false, true, false, (float) 1 / 600, 1.0F,
                    WeightedList.<ExplosionParticleInfo>builder().build(),
                    0, 0, 0
            );
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level() instanceof ServerLevel serverWorld) {
            explosionPower = (float) serverWorld.getGameRules().get(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER) / 10;
            this.explode(
                    0.5F,
                    true, false, true,
                    0F, 1.0F,
                    WeightedList.<ExplosionParticleInfo>builder().build(),
                    0, 0, 0
            );
            this.explode(
                    12F,
                    false, false, false,
                    0F, 0F,
                    WeightedList.<ExplosionParticleInfo>builder()
                            .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
                            .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
                            .build(),
                    0, 0, 0
            );
            this.explode(
                    this.explosionPower,
                    false, true, false,
                    (float) 1 / 600, 1.0F,
                    WeightedList.<ExplosionParticleInfo>builder().build(),
                    0, 0, 0
            );
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

    private void explode(float power, boolean destroyBlocks, boolean damageEntities, boolean createFire, float damageFactor, float kbModifierFactor, WeightedList<ExplosionParticleInfo> blockParticles, double dx, double dy, double dz) {
        if (level() instanceof ServerLevel serverWorld) {
            FireballExplosionImpl.createExplosion(
                    this.level(),
                    this,
                    serverWorld.damageSources().explosion(this, this.getOwner()),
                    new SimpleExplosionDamageCalculator(
                            destroyBlocks,
                            damageEntities,
                            Optional.of(explosionDamage),
                            Optional.empty()
                    ) {
                        @Override
                        public float getEntityDamageAmount(Explosion explosion, Entity entity, float amount) {
                            return super.getEntityDamageAmount(explosion, entity, amount) * damageFactor;
                        }

                        @Override
                        public float getKnockbackMultiplier(Entity entity) {
                            return super.getKnockbackMultiplier(entity) * kbModifierFactor;
                        }
                    },
                    this.getX() + dx,
                    this.getY() + dy,
                    this.getZ() + dz,
                    power,
                    createFire,
                    Level.ExplosionInteraction.MOB,
                    ParticleTypes.EXPLOSION,
                    ParticleTypes.EXPLOSION_EMITTER,
                    blockParticles,
                    SoundEvents.GENERIC_EXPLODE

            );
        }
    }
}
