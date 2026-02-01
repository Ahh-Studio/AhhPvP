package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

public class MurdererEntity extends Monster {
    private int wTapFreezeTicks;
    private static final int DEFAULT_FREEZE_DURATION = 4;
    private int enderPearlCooldownTicks = 0;
    private static final int ENDER_PEARL_COOLDOWN = 200;
    private int comboHitsTaken = 0;
    private int comboTickCount = 40;
    private int fireballCooldownTicks = 0;
    private static final int FIREBALL_COOLDOWN = 100;
    private boolean isDoingWaterBucketMLG = false;
    private BlockPos waterBucketMLGWaterPos;
    public boolean isInPhase2 = false;

    public MurdererEntity(EntityType<? extends MurdererEntity> type, Level world) {
        super(ModEntityTypes.MURDERER, world);
        this.wTapFreezeTicks = 0;
    }

    private void triggerWTapPause() {
        this.wTapFreezeTicks = DEFAULT_FREEZE_DURATION;
        this.getNavigation().stop();
    }

    @Override
    public boolean canSimulateMovement() {
        return this.wTapFreezeTicks <= 0 && super.canSimulateMovement();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.wTapFreezeTicks > 0) {
            this.wTapFreezeTicks--;
        }

        if (this.isInPhase2) {
            if (this.getAttributeBaseValue(Attributes.ENTITY_INTERACTION_RANGE) != 4.0) {
                Objects.requireNonNull(this.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)).setBaseValue(4.0);
            }
            if (this.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) != 12.0) {
                Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(12.0);
            }
            if (this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) != 1.0) {
                Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(1.0);
            }
            return;
        }

        if (this.enderPearlCooldownTicks > 0) {
            this.enderPearlCooldownTicks--;
        }

        if (this.fireballCooldownTicks > 0) {
            this.fireballCooldownTicks--;
        }

        if (this.comboTickCount > 0) this.comboTickCount--;
        else {
            this.comboTickCount = 40;
            this.comboHitsTaken = 0;
        }

        if (this.comboHitsTaken > 3 && this.getTarget() != null) {
            if (this.level() instanceof ServerLevel serverWorld) {
                FireballEntity fireballEntity = new FireballEntity(this, this.level(), ModItems.FIREBALL.getDefaultInstance());
                fireballEntity.setPosRaw(this.getX(), this.getEyeY(), this.getZ());

                Vec3 targetPos = this.getTarget().position().subtract(0, 0.5, 0);
                Vec3 mobPos = this.getEyePosition();
                Vec3 direction = targetPos.subtract(mobPos).normalize();

                this.lookAt(this.getTarget(), 30.0F, 30.0F);

                fireballEntity.shoot(
                        1.2 * direction.x,
                        1.2 * direction.y,
                        1.2 * direction.z,
                        1.2F,
                        1.0F
                );

                serverWorld.addFreshEntity(fireballEntity);
                this.comboHitsTaken = 0;
            }
        }

        if (this.getTarget() != null) {
            this.placeBlocksUnderFeetWhenBeBlocked();
        }

        // water bucket MLG
        {
            BlockState blockState = this.level().getBlockState(new BlockPos(
                    this.position().x >= 0 ? (int) this.position().x : (int) this.position().x - 1,
                    ((int) this.position().y) - 1,
                    this.position().z >= 0 ? (int) this.position().z : (int) this.position().z - 1
            ));

            BlockState blockState1 = this.level().getBlockState(new BlockPos(
                    this.position().x >= 0 ? (int) this.position().x : (int) this.position().x - 1,
                    ((int) this.position().y) - 2,
                    this.position().z >= 0 ? (int) this.position().z : (int) this.position().z - 1
            ));

            if (this.fallDistance > 3 && blockState.isAir() && !blockState1.isAir()) {
                this.waterBucketMLGWaterPos = new BlockPos(
                        this.position().x >= 0 ? (int) this.position().x : (int) this.position().x - 1,
                        ((int) this.position().y) - 1,
                        this.position().z >= 0 ? (int) this.position().z : (int) this.position().z - 1
                );
                this.level().setBlock(
                        this.waterBucketMLGWaterPos,
                        Blocks.WATER.defaultBlockState(),
                        6
                );
                this.isDoingWaterBucketMLG = true;
            }

            BlockState blockState2 = this.level().getBlockState(new BlockPos(
                    this.position().x >= 0 ? (int) this.position().x : (int) this.position().x - 1,
                    (int) this.position().y - 1,
                    this.position().z >= 0 ? (int) this.position().z : (int) this.position().z - 1
            ));

            if (this.isDoingWaterBucketMLG
                    && this.waterBucketMLGWaterPos != null
                    && this.fallDistance == 0
                    && this.level().getBlockState(this.waterBucketMLGWaterPos).is(Blocks.WATER)
                    && !blockState2.isAir()
            ) {
                this.level().setBlock(
                        this.waterBucketMLGWaterPos,
                        Blocks.AIR.defaultBlockState(),
                        6
                );
                this.isDoingWaterBucketMLG = false;
                this.waterBucketMLGWaterPos = null;
            }
        }

        if ((4 * (this.getHealth() + this.getAbsorptionAmount())) < (this.getMaxHealth() + this.getAbsorptionAmount())) {
            this.setInPhase2(true);
        }
    }

    private void placeBlocksUnderFeetWhenBeBlocked() {
        if (!this.hasLineOfSight(this.getTarget())) { // 看不到目标
            if (this.getRandom().nextIntBetweenInclusive(0, 10) <= 0.5) { // 随机，有概率不触发
                if (this.level() instanceof ServerLevel serverWorld) { // 服务端运作
                    final BlockState blockState = this.level().getBlockState(new BlockPos(
                            this.position().x >= 0 ? (int) this.position().x : (int) this.position().x - 1,
                            ((int) this.position().y) - 1,
                            this.position().z >= 0 ? (int) this.position().z : (int) this.position().z - 1
                    ));

                    if (!blockState.isAir()) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.5, 0.0));
                        this.level().setBlock(
                                new BlockPos(
                                        this.position().x >= 0 ? (int) this.position().x : (int) this.position().x - 1,
                                        (int) this.position().y,
                                        this.position().z >= 0 ? (int) this.position().z : (int) this.position().z - 1
                                ),
                                Blocks.DIRT.defaultBlockState(),
                                6
                        );
                    }
                }
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (source.getEntity() != null && source.getEntity() instanceof LivingEntity) this.comboHitsTaken++;
        return super.hurtServer(world, source, amount);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new EnderPearlTeleportGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new DaggerAttackGoal(this, 1.0, 20, 30.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Mob.class, true));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder createMurdererAttributes() {
        return new AttributeSupplier.Builder()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ARMOR_TOUGHNESS, 4)
                .add(Attributes.MAX_ABSORPTION)
                .add(Attributes.STEP_HEIGHT)
                .add(Attributes.SCALE)
                .add(Attributes.GRAVITY)
                .add(Attributes.SAFE_FALL_DISTANCE)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER)
                .add(Attributes.JUMP_STRENGTH)
                .add(Attributes.OXYGEN_BONUS)
                .add(Attributes.BURNING_TIME)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY)
                .add(Attributes.MOVEMENT_EFFICIENCY)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.CAMERA_DISTANCE)
                .add(Attributes.WAYPOINT_TRANSMIT_RANGE)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ATTACK_SPEED, 10)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3);
    }

    public State getState() {
        if (this.isAggressive()) {
            return State.ATTACKING;
        } else {
            return State.IDLE;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData entityData2 = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        this.getNavigation().setCanOpenDoors(true);
        RandomSource random = world.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        return entityData2;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        ItemStack helmet = Items.LEATHER_HELMET.getDefaultInstance();
        ItemStack chestplate = Items.LEATHER_CHESTPLATE.getDefaultInstance();
        ItemStack leggings = Items.DIAMOND_LEGGINGS.getDefaultInstance();
        ItemStack boots = Items.DIAMOND_BOOTS.getDefaultInstance();

        helmet.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        chestplate.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        leggings.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        boots.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.THROWABLE_DAGGER));
        this.setItemSlot(EquipmentSlot.HEAD, helmet);
        this.setItemSlot(EquipmentSlot.CHEST, chestplate);
        this.setItemSlot(EquipmentSlot.LEGS, leggings);
        this.setItemSlot(EquipmentSlot.FEET, boots);
    }

    public void setInPhase2(boolean inPhase2) {
        this.isInPhase2 = inPhase2;
    }

    public enum State {
        IDLE,
        ATTACKING,
        NEUTRAL
    }

    static class EnderPearlTeleportGoal extends Goal {
        private final MurdererEntity mob;

        public EnderPearlTeleportGoal(MurdererEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && this.mob.getTarget().isAlive()
                    && this.mob.distanceToSqr(this.mob.getTarget()) > 400
                    && this.mob.enderPearlCooldownTicks <= 0
                    && !this.mob.level().isClientSide()
                    && !this.mob.isUnderWater();
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() == null) return;

            Level world = this.mob.level();
            if (world.isClientSide()) return;

            ThrownEnderpearl enderPearl = new ThrownEnderpearl(EntityType.ENDER_PEARL, world);
            enderPearl.setOwner(this.mob);
            enderPearl.setPosRaw(mob.getX(), mob.getEyeY(), mob.getZ());

            Vec3 targetPos = this.mob.getTarget().getEyePosition();
            Vec3 mobPos = this.mob.getEyePosition();
            Vec3 direction = targetPos.subtract(mobPos).normalize();

            double i = direction.y * 1.2;

            if (i >= 0) {
                enderPearl.shoot(
                        1.2 * direction.x, 2.0 * i + 0.5, 1.2 * direction.z,
                        1.2F, 1.0F
                );
            } else if (i > -0.4 && i < -0.1) {
                enderPearl.shoot(
                        1.2 * direction.x, 1 / i * -0.05, 1.2 * direction.z,
                        1.2F, 1.0F
                );
            } else {
                enderPearl.shoot(
                        1.2 * direction.x, 0.25 * i, 1.2 * direction.z,
                        1.2F, 1.0F
                );
            }

            this.mob.playSound(SoundEvents.ENDER_PEARL_THROW, 1.0F, 1.0F);
            this.mob.lookAt(this.mob.getTarget(), 30.0F, 30.0F);

            world.addFreshEntity(enderPearl);

            this.mob.enderPearlCooldownTicks = ENDER_PEARL_COOLDOWN;
            this.mob.getNavigation().stop();
            this.mob.lookAt(this.mob.getTarget(), 30.0F, 30.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    static class MeleeAttackGoal extends Goal {
        protected final PathfinderMob mob;
        private final double speed;
        private final boolean pauseWhenMobIdle;
        private Path path;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int updateCountdownTicks;
        private int cooldown;
        private long lastUpdateTime;

        public MeleeAttackGoal(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
            this.mob = mob;
            this.speed = speed;
            this.pauseWhenMobIdle = pauseWhenMobIdle;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            long l = this.mob.level().getGameTime();
            if (l - this.lastUpdateTime < 20L) {
                return false;
            } else {
                this.lastUpdateTime = l;
                LivingEntity livingEntity = this.mob.getTarget();
                if (livingEntity == null) {
                    return false;
                } else if (!livingEntity.isAlive()) {
                    return false;
                } else {
                    this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                    return (this.path != null || this.mob.isWithinMeleeAttackRange(livingEntity)) && this.mob.distanceToSqr(livingEntity) <= 100;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (!this.pauseWhenMobIdle) {
                return !this.mob.getNavigation().isDone();
            } else {
                return this.mob.isWithinHome(livingEntity.blockPosition()) && !(livingEntity instanceof Player playerEntity && (playerEntity.isSpectator() || playerEntity.isCreative()));
            }
        }

        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.path, this.speed);
            this.mob.setAggressive(true);
            this.updateCountdownTicks = 0;
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                this.mob.setTarget(null);
            }

            this.mob.setAggressive(false);
            this.mob.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
                this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
                if ((this.pauseWhenMobIdle || this.mob.getSensing().hasLineOfSight(livingEntity))
                        && this.updateCountdownTicks <= 0
                        && (
                        this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
                                || livingEntity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0
                                || this.mob.getRandom().nextFloat() < 0.05F
                )) {
                    this.targetX = livingEntity.getX();
                    this.targetY = livingEntity.getY();
                    this.targetZ = livingEntity.getZ();
                    this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                    double d = this.mob.distanceToSqr(livingEntity);
                    if (d > 1024.0) {
                        this.updateCountdownTicks += 10;
                    } else if (d > 256.0) {
                        this.updateCountdownTicks += 5;
                    }

                    if (!this.mob.getNavigation().moveTo(livingEntity, this.speed)) {
                        this.updateCountdownTicks += 15;
                    }

                    this.updateCountdownTicks = this.adjustedTickDelay(this.updateCountdownTicks);
                }

                this.cooldown = Math.max(this.cooldown - 1, 0);
                this.attack(livingEntity);
            }
        }

        protected void attack(LivingEntity target) {
            if (this.canAttack(target)) {
                this.resetCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(getServerLevel(this.mob), target);
                if (this.mob instanceof MurdererEntity murderer) {
                    murderer.triggerWTapPause();
                }
            }
        }

        protected void resetCooldown() {
            double attackSpeed = this.mob.getAttributeValue(Attributes.ATTACK_SPEED);
            int dynamicCooldown = (int) (20 / attackSpeed);
            this.cooldown = this.adjustedTickDelay(Math.max(1, dynamicCooldown));
        }

        protected boolean isCooledDown() {
            return this.cooldown <= 0;
        }

        protected boolean canAttack(LivingEntity target) {
            return this.isCooledDown() && this.mob.isWithinMeleeAttackRange(target) && this.mob.getSensing().hasLineOfSight(target);
        }

        protected int getCooldown() {
            return this.cooldown;
        }

        protected int getMaxCooldown() {
            double attackSpeed = this.mob.getAttributeValue(Attributes.ATTACK_SPEED);
            int dynamicCooldown = (int) (20 / attackSpeed);
            return this.adjustedTickDelay(Math.max(1, dynamicCooldown));
        }
    }

    static class DaggerAttackGoal extends Goal {
        private final MurdererEntity actor;
        private final double speed;
        private int attackInterval;
        private final float squaredRange;
        private int cooldown = -1;
        private int targetSeeingTicker;
        private boolean movingToLeft;
        private boolean backward;
        private int combatTicks = -1;

        public DaggerAttackGoal(MurdererEntity actor, double speed, int attackInterval, float range) {
            this.actor = actor;
            this.speed = speed;
            this.attackInterval = attackInterval;
            this.squaredRange = range * range;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public void setAttackInterval(int attackInterval) {
            this.attackInterval = attackInterval;
        }

        @Override
        public boolean canUse() {
            return this.actor.getTarget() != null && this.isHoldingDagger()
                    && this.actor.getTarget().distanceToSqr(this.actor) > 100
                    && this.actor.getTarget().distanceToSqr(this.actor) <= 400;
        }

        protected boolean isHoldingDagger() {
            return this.actor.isHolding(ModItems.THROWABLE_DAGGER);
        }

        @Override
        public boolean canContinueToUse() {
            return (this.canUse() || !this.actor.getNavigation().isDone()) && this.isHoldingDagger();
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
            this.targetSeeingTicker = 0;
            this.cooldown = -1;
            this.actor.stopUsingItem();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.actor.getTarget();
            if (livingEntity != null) {
                double d = this.actor.distanceToSqr(livingEntity); //
                boolean bl = this.actor.getSensing().hasLineOfSight(livingEntity);
                boolean bl2 = this.targetSeeingTicker > 0;
                this.actor.lookAt(livingEntity, 30.0F, 30.0F);

                // Approach by fireball
                BlockState blockState = this.actor.level().getBlockState(new BlockPos(
                        this.actor.position().x >= 0 ? (int) this.actor.position().x : (int) this.actor.position().x - 1,
                        (int) this.actor.position().y - 1,
                        this.actor.position().z >= 0 ? (int) this.actor.position().z : (int) this.actor.position().z - 1
                ));
                if (this.actor.getHealth() >= 10 && !blockState.isAir() && this.actor.fireballCooldownTicks <= 0) { // 同时满足：血量>10、脚底方块不是空气、冷却结束
                    if (this.actor.getRandom().nextIntBetweenInclusive(1, 10) <= 5) { // 随机，有概率不触发
                        if (this.actor.level() instanceof ServerLevel serverWorld) { // 服务端生成实体
                            FireballEntity fireballEntity = new FireballEntity(this.actor, this.actor.level(), ModItems.FIREBALL.getDefaultInstance());
                            fireballEntity.setPosRaw(this.actor.getX(), this.actor.getEyeY(), this.actor.getZ());

                            this.actor.lookAt(this.actor.getTarget(), 30.0F, 30.0F);

                            this.actor.getMoveControl().strafe(5.0F, 0.0F);
                            this.actor.setDeltaMovement(this.actor.getDeltaMovement().add(0.0, 0.25, 0.0));

                            fireballEntity.shoot(
                                    0.6 * Mth.sin(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180)),
                                    -1.0,
                                    0.6 * -Mth.cos(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180)),
                                    1.2F, 0.0F
                            );

                            serverWorld.addFreshEntity(fireballEntity);

                            this.actor.fireballCooldownTicks = FIREBALL_COOLDOWN;
                        }
                    }
                }

                if (bl != bl2) {
                    this.targetSeeingTicker = 0;
                }

                if (bl) {
                    this.targetSeeingTicker++;
                } else {
                    this.targetSeeingTicker--;
                }

                if (!(d > this.squaredRange) && this.targetSeeingTicker >= 20) {
                    this.actor.getNavigation().stop();
                    this.combatTicks++;
                } else {
                    this.combatTicks = -1;
                }

                if (this.combatTicks >= 20) {
                    if (this.actor.getRandom().nextFloat() < 0.3) {
                        this.movingToLeft = !this.movingToLeft;
                    }

                    if (this.actor.getRandom().nextFloat() < 0.3) {
                        this.backward = !this.backward;
                    }

                    this.combatTicks = 0;
                }

                if (this.combatTicks > -1) {
                    if (d > this.squaredRange * 0.75F) {
                        this.backward = false;
                    } else if (d < this.squaredRange * 0.25F) {
                        this.backward = true;
                    }

                    this.actor.getMoveControl().strafe(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
                    if (this.actor.getControlledVehicle() instanceof Mob mobEntity) {
                        mobEntity.lookAt(livingEntity, 30.0F, 30.0F);
                    }

                    this.actor.lookAt(livingEntity, 30.0F, 30.0F);
                } else {
                    this.actor.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
                }

                if (this.actor.isUsingItem()) {
                    if (!bl && this.targetSeeingTicker < -60) {
                        this.actor.stopUsingItem();
                    } else if (bl) {
                        int i = this.actor.getTicksUsingItem();
                        if (i >= 20) {
                            this.actor.stopUsingItem();

                            DaggerEntity daggerEntity = getDaggerEntity();
                            this.actor.level().addFreshEntity(daggerEntity);

                            this.actor.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.actor.getRandom().nextFloat() * 0.4F + 0.8F));


                            this.cooldown = this.attackInterval;
                        }
                    }
                } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                    this.actor.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.actor, ModItems.THROWABLE_DAGGER));
                }
            }
        }

        private @NotNull DaggerEntity getDaggerEntity() {
            DaggerEntity daggerEntity = new DaggerEntity(ModEntityTypes.DAGGER, this.actor.level());
            daggerEntity.setOwner(this.actor);

            daggerEntity.setPosRaw(this.actor.getX(), this.actor.getEyeY(), this.actor.getZ());

            float f = -Mth.sin(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180));
            float g = -Mth.sin((this.actor.getXRot() + 0.0F) * ((float)Math.PI / 180));
            float h = Mth.cos(this.actor.getYRot() * ((float)Math.PI / 180)) * Mth.cos(this.actor.getXRot() * ((float)Math.PI / 180));
            daggerEntity.shoot(f, g, h, 1.2F, 0.0F);
            return daggerEntity;
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel world, DamageSource source, boolean causedByPlayer) {}
}
