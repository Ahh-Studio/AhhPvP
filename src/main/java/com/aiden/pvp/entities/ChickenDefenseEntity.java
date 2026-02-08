package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class ChickenDefenseEntity extends Animal {
    private static final Logger log = LogManager.getLogger(ChickenDefenseEntity.class);
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private int attackCoolDown = 0;

    public ChickenDefenseEntity(EntityType<? extends ChickenDefenseEntity> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.goalSelector.addGoal(2, new EggllitAttackGoal(this));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    static class EggllitAttackGoal extends Goal {
        private final ChickenDefenseEntity actor;

        public EggllitAttackGoal(ChickenDefenseEntity actor) {
            this.actor = actor;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.actor.getTarget() != null && this.actor.getTarget().isAlive()
                    && this.actor.getTarget().distanceToSqr(this.actor) <= 400
                    && this.actor.attackCoolDown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.actor.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.actor.setAggressive(false);
            this.actor.stopUsingItem();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.actor.getTarget();
            if (target == null || !target.isAlive()) return;

            this.actor.lookAt(target, 30.0F, 30.0F);
            this.shootEgg();
        }

        private void shootEgg() {
            this.actor.attackCoolDown = 4;

            float f = -Mth.sin(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180));
            float g = -Mth.sin((this.actor.getXRot() + 0.0F) * ((float)Math.PI / 180));
            float h = Mth.cos(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180));

            EgglletEntity egglletEntity = new EgglletEntity(ModEntityTypes.EGGLLIT,
                    this.actor.getX(), this.actor.getEyeY(), this.actor.getZ(),
                    this.actor.level(), ModItems.EGGLLET.getDefaultInstance()
            );

            egglletEntity.setOwner(this.actor);
            egglletEntity.shoot(f, g, h, 1.2F, 0.0F);

            this.actor.level().addFreshEntity(egglletEntity);

            this.actor.playSound(SoundEvents.EGG_THROW, 1.0F, 1.0F / (this.actor.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.attackCoolDown > 0) {
            this.attackCoolDown--;
        }

        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = this.flapSpeed + (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0) {
            this.setDeltaMovement(vec3.multiply(1.0, 0.6, 1.0));
        }

        this.flap = this.flap + this.flapping * 2.0F;
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource damageSource) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    protected void playStepSound(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Nullable
    public ChickenDefenseEntity getBreedOffspring(@NonNull ServerLevel serverLevel, @NonNull AgeableMob ageableMob) {
        return ModEntityTypes.CHICKEN_DEFENSE.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.CHICKEN_FOOD);
    }

    @Override
    protected int getBaseExperienceReward(@NonNull ServerLevel serverLevel) {
        return 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void positionRider(@NonNull Entity entity, Entity.@NonNull MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).yBodyRot = this.yBodyRot;
        }
    }
}
