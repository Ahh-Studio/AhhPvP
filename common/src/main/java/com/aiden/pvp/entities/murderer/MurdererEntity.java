package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.util.enchant.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class MurdererEntity extends Monster {
    public final MurdererCombatManager combatManager = new MurdererCombatManager(this);

    int enderPearlCooldownTicks = 0;
    static final int ENDER_PEARL_MAX_COOLDOWN = 200;
    static final int ENDER_PEARL_MIN_COOLDOWN = 40;
    int fireballCooldownTicks = 0;
    static final int FIREBALL_MAX_COOLDOWN = 100;
    static final int FIREBALL_MIN_COOLDOWN = 20;
    /** 烈焰弹跳跃的独立冷却，不与定向烈焰弹共享，保证能高频进近 */
    int fireballJumpCooldownTicks = 0;
    static final int FIREBALL_JUMP_MAX_COOLDOWN = 45;
    static final int FIREBALL_JUMP_MIN_COOLDOWN = 25;

    private static final int FIREBALL_HOLD_TICKS = 12;
    private int heldFireballTicks = 0;

    int eatingGoldenAppleTicks = 0;
    /** 补血模式剩余金苹果库存（每次吃 1 个，吃光则本次生命不再补血） */
    public int goldenApples = 16;
    /** 是否处于"战斗中补血"模式（供伤害钩子判断） */
    public boolean inHealMode = false;
    /** 补血模式下最近一次扣血的 tick，供 GoldenAppleHealGoal 判断是否撤退 */
    private int healModeTookDamageTick = -1;

    private static final int COMBO_RESET_TICKS = 40;

    private int wTapFreezeTicks;
    private static final int DEFAULT_FREEZE_DURATION = 3;
    int comboHitsTaken = 0;
    private int comboTickCount = COMBO_RESET_TICKS;
    public boolean jumpResetQueued = false;
    private boolean doingWaterBucketMLG = false;
    private BlockPos waterBucketMLGWaterPos;
    private BlockPos fireExtinguishWaterPos;
    private byte placeBlockCD = 0;
    private static final double AERIAL_CHASE_FORCE = 0.04;
    private static final int PLACE_BLOCK_CD = 8;
    private static final float FIRE_EXTINGUISH_CHANCE = 0.05F;

    public MurdererEntity(EntityType<? extends MurdererEntity> type, Level world) {
        super(type, world);
        this.wTapFreezeTicks = 0;
    }

    public boolean isEatingGoldenApple() {
        return this.eatingGoldenAppleTicks > 0;
    }

    /** 补血模式下是否刚被扣血（供 GoldenAppleHealGoal 判断撤退，读取后即消费） */
    boolean inHealModeTookDamage() {
        boolean took = this.healModeTookDamageTick != -1
                && Math.abs(this.healModeTookDamageTick - this.tickCount) <= 1;
        this.healModeTookDamageTick = -1;
        return took;
    }

    public State getState() {
        if (this.isAggressive()) {
            return State.ATTACKING;
        } else {
            return State.IDLE;
        }
    }

    @Override
    public boolean canSimulateMovement() {
        return this.wTapFreezeTicks <= 0 && super.canSimulateMovement();
    }

    void triggerWTapPause() {
        this.wTapFreezeTicks = DEFAULT_FREEZE_DURATION;
        this.getNavigation().stop();
    }

    void holdFireballInHand() {
        this.heldFireballTicks = FIREBALL_HOLD_TICKS;
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FIREBALL));
    }

    @Override
    public void tick() {
        super.tick();

        // 先更新战斗决策，保证各攻击 Goal 读到的是最新动作
        this.combatManager.update();

        this.tickCooldowns();
        this.handleAerialPursuit();
        this.handleSprinting();

        // 看不到目标时，有机会在脚底铺方块垫高
        if (this.getTarget() != null) {
            this.placeBlocksUnderFeetWhenBeBlocked();
            this.placeBlockCD = PLACE_BLOCK_CD;
        }

        this.doWaterBucketMLG();
        this.tryAutoExtinguishFire();
    }

    /** 统一递减所有冷却与临时状态计数 */
    private void tickCooldowns() {
        if (this.wTapFreezeTicks > 0) this.wTapFreezeTicks--;
        if (this.enderPearlCooldownTicks > 0) this.enderPearlCooldownTicks--;
        if (this.fireballCooldownTicks > 0) this.fireballCooldownTicks--;
        if (this.fireballJumpCooldownTicks > 0) this.fireballJumpCooldownTicks--;
        if (this.eatingGoldenAppleTicks > 0) this.eatingGoldenAppleTicks--;
        if (this.heldFireballTicks > 0) {
            this.heldFireballTicks--;
            if (this.heldFireballTicks == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.THROWABLE_DAGGER));
            }
        }
        if (this.placeBlockCD > 0) this.placeBlockCD--;
        if (this.comboTickCount > 0) {
            this.comboTickCount--;
        } else {
            this.comboTickCount = COMBO_RESET_TICKS;
            this.comboHitsTaken = 0;
        }
    }

    /** 空中追击：未落地且有目标时，也持续向目标水平移动 */
    private void handleAerialPursuit() {
        if (!this.onGround() && this.getTarget() != null && this.wTapFreezeTicks <= 0) {
            Vec3 toTarget = this.getTarget().position().subtract(this.position());
            double horizontalDist = toTarget.horizontalDistance();
            if (horizontalDist > 0.01) {
                Vec3 dir = toTarget.multiply(1.0 / horizontalDist, 0, 1.0 / horizontalDist);
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(
                        motion.x + dir.x * AERIAL_CHASE_FORCE,
                        motion.y,
                        motion.z + dir.z * AERIAL_CHASE_FORCE
                );
            }
        }
    }

    /** 接近目标时持续疾跑：移速+30%(0.1→0.13)，与玩家疾跑一致 */
    private void handleSprinting() {
        if (!this.level().isClientSide()) {
            LivingEntity chaseTarget = this.getTarget();
            this.setSprinting(chaseTarget != null
                    && chaseTarget.isAlive()
                    && this.onGround()
                    && !this.getNavigation().isDone()
                    && this.wTapFreezeTicks <= 0);
        }
    }

    /** 被堵时偶尔在脚底铺方块以垫高自己 */
    private void placeBlocksUnderFeetWhenBeBlocked() {
        // 看不到目标
        if (this.hasLineOfSight(Objects.requireNonNull(this.getTarget()))) return;
        // 随机，有概率不触发
        if (this.getRandom().nextInt(0, 100) == 0) {
            // 服务端运作
            if (this.level() instanceof ServerLevel) {
                final BlockState blockState = this.level().getBlockState(new BlockPos(
                        Mth.floor(this.position().x),
                        ((int) this.position().y) - 1,
                        Mth.floor(this.position().z)
                ));

                if (!blockState.isAir()) {
                    this.jumpFromGround();
                    this.level().setBlock(
                            new BlockPos(
                                    Mth.floor(this.position().x),
                                    (int) this.position().y,
                                    Mth.floor(this.position().z)
                            ),
                            Blocks.DIRT.defaultBlockState(),
                            6
                    );
                }
            }
        }
    }

    /** 摔落自救（MLG 水桶）：摔落时在脚下铺水，落地后自动收水 */
    private void doWaterBucketMLG() {
        BlockPos blockPos = new BlockPos(
                Mth.floor(this.position().x),
                Mth.floor(this.position().y) - 1,
                Mth.floor(this.position().z)
        );
        BlockState blockState = this.level().getBlockState(blockPos);
        BlockState blockState1 = this.level().getBlockState(blockPos.below());

        // 摔落距离>3、脚下方块是空气、脚下第二格不是空气
        if (!this.doingWaterBucketMLG && this.fallDistance > 3 && blockState.isAir() && !blockState1.isAir()) {
            this.waterBucketMLGWaterPos = blockPos;
            this.level().setBlock(
                    this.waterBucketMLGWaterPos,
                    Blocks.WATER.defaultBlockState(),
                    6
            );
            this.doingWaterBucketMLG = true;
        } else if (this.doingWaterBucketMLG && this.waterBucketMLGWaterPos != null && !this.level().getBlockState(this.waterBucketMLGWaterPos).is(Blocks.WATER)) {
            // 熔断：水被外部因素移除，清理状态
            this.doingWaterBucketMLG = false;
            this.waterBucketMLGWaterPos = null;
        } else if (this.waterBucketMLGWaterPos != null && this.fallDistance == 0 && this.level().getBlockState(this.waterBucketMLGWaterPos).is(Blocks.WATER)) {
            this.level().setBlock(
                    this.waterBucketMLGWaterPos,
                    Blocks.AIR.defaultBlockState(),
                    6
            );
            this.doingWaterBucketMLG = false;
            this.waterBucketMLGWaterPos = null;
        }
    }

    /**
     * 自动灭火：着火时如果脚下有方块，每 tick 有概率在脚底放水；若仍燃烧，
     * 尝试破坏脚部周围 3x3x3 内能看到的火方块。熄灭后自动收水。
     */
    private void tryAutoExtinguishFire() {
        if (this.isOnFire()) {
            BlockPos belowPos = new BlockPos(
                    Mth.floor(this.position().x),
                    Mth.floor(this.position().y) - 1,
                    Mth.floor(this.position().z)
            );
            if (!this.level().getBlockState(belowPos).isAir() && this.getRandom().nextFloat() < FIRE_EXTINGUISH_CHANCE) {
                if (this.level() instanceof ServerLevel) {
                    BlockPos feetPos = new BlockPos(
                            Mth.floor(this.position().x),
                            Mth.floor(this.position().y),
                            Mth.floor(this.position().z)
                    );
                    this.level().setBlock(feetPos, Blocks.WATER.defaultBlockState(), 6);
                    this.fireExtinguishWaterPos = feetPos;
                }
            }
            // 破坏脚部周围 3x3x3 内能看到的火方块
            if (this.level() instanceof ServerLevel serverLevel) {
                BlockPos feetPos = new BlockPos(
                        Mth.floor(this.position().x),
                        Mth.floor(this.position().y),
                        Mth.floor(this.position().z)
                );
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            BlockPos targetPos = feetPos.offset(dx, dy, dz);
                            if (this.level().getBlockState(targetPos).is(Blocks.FIRE)) {
                                Vec3 eyePos = this.getEyePosition();
                                Vec3 targetCenter = Vec3.atCenterOf(targetPos);
                                if (this.level().clip(new ClipContext(eyePos, targetCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS) {
                                    this.level().setBlock(targetPos, Blocks.AIR.defaultBlockState(), 6);
                                }
                            }
                        }
                    }
                }
            }
        } else if (this.fireExtinguishWaterPos != null) {
            if (this.level() instanceof ServerLevel && this.level().getBlockState(this.fireExtinguishWaterPos).is(Blocks.WATER)) {
                this.level().setBlock(this.fireExtinguishWaterPos, Blocks.AIR.defaultBlockState(), 6);
            }
            this.fireExtinguishWaterPos = null;
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel world, DamageSource source, float amount) {
        // 补血模式下任意扣血都触发撤退（供 GoldenAppleHealGoal 感知）
        if (this.inHealMode) this.healModeTookDamageTick = this.tickCount;
        if (source.getEntity() != null && source.getEntity() instanceof LivingEntity) this.comboHitsTaken++;
        // 每次地面受击有 50% 概率施出 Jump Reset（空中受击自带弱击退分支，不叠加）
        this.jumpResetQueued = !world.isClientSide() && this.onGround() && this.getRandom().nextFloat() < 0.5F;
        if (this.jumpResetQueued) this.jumpFromGround();
        boolean result = super.hurtServer(world, source, amount);
        this.jumpResetQueued = false;
        return result;
    }

    @Override
    public boolean doHurtTarget(@NonNull ServerLevel level, @NonNull Entity target) {
        boolean result = super.doHurtTarget(level, target);
        if (result && this.isSprinting() && target instanceof LivingEntity livingTarget) {
            // 疾跑时额外击退，类似玩家疾跑攻击
            livingTarget.knockback(
                    0.5,
                    Mth.sin(this.getYRot() * ((float) Math.PI / 180)),
                    -Mth.cos(this.getYRot() * ((float) Math.PI / 180))
            );
        }
        return result;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 按优先级从高到低排列，便于阅读与平衡
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GoldenAppleHealGoal(this));
        this.goalSelector.addGoal(2, new EnderPearlTeleportGoal(this));
        this.goalSelector.addGoal(2, new FireballJumpGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new DaggerAttackGoal(this, MurdererCombatManager.CHASE_SPEED, 20, 30.0F));
        this.goalSelector.addGoal(3, new FireballAttackGoal(this));
        this.goalSelector.addGoal(3, new StuckMiningAttackGoal(this));
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
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 90.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ATTACK_SPEED, 10)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NonNull ServerLevelAccessor world, @NonNull DifficultyInstance difficulty, @NonNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData entityData2 = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        this.getNavigation().setCanOpenDoors(true);
        RandomSource random = world.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.setCustomName(Component.literal(FULL_NAMES[random.nextInt(FULL_NAMES.length)]));
        return entityData2;
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NonNull RandomSource random, @NonNull DifficultyInstance localDifficulty) {
        if (level() instanceof ServerLevel serverLevel) {
            ItemStack helmet = Items.LEATHER_HELMET.getDefaultInstance();
            ItemStack chestplate = Items.LEATHER_CHESTPLATE.getDefaultInstance();
            ItemStack leggings = Items.DIAMOND_LEGGINGS.getDefaultInstance();
            ItemStack boots = Items.DIAMOND_BOOTS.getDefaultInstance();

            helmet.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            chestplate.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            leggings.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            boots.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.THROWABLE_DAGGER));
            this.setItemSlot(EquipmentSlot.HEAD, EnchantmentUtil.enchantItemStack(serverLevel, helmet, Enchantments.PROTECTION, 4));
            this.setItemSlot(EquipmentSlot.CHEST, EnchantmentUtil.enchantItemStack(serverLevel, chestplate, Enchantments.PROTECTION, 4));
            this.setItemSlot(EquipmentSlot.LEGS, EnchantmentUtil.enchantItemStack(serverLevel, leggings, Enchantments.PROTECTION, 4));
            this.setItemSlot(EquipmentSlot.FEET, EnchantmentUtil.enchantItemStack(serverLevel, boots, Enchantments.PROTECTION, 4));
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected @NonNull SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected @NonNull SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel world, DamageSource source, boolean causedByPlayer) {}

    private static final String[] FIRST_NAMES = {
            "Emma", "Liam", "Olivia", "Noah", "Ava",
            "Elijah", "Sophia", "Lucas", "Mia", "Ethan",
            "Charlotte", "Benjamin", "Amelia", "Henry", "Evelyn",
            "Sebastian", "Abigail", "Jack", "Elizabeth", "Samuel",
            "Camila", "Daniel", "Gianna", "Matthew", "Luna",
            "David", "Hannah", "Joseph", "Scarlett", "Owen",
            "Grace", "Leo", "Chloe", "Jackson", "Victoria",
            "Wyatt", "Aurora", "Julian", "Sofia", "Grayson"
    };

    private static final String[] LAST_NAMES = {
            "Johnson", "Smith", "Williams", "Brown", "Jones",
            "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Wilson", "Anderson", "Thomas", "Taylor",
            "Moore", "Jackson", "Martin", "Lee", "Perez",
            "Thompson", "White", "Harris", "Clark", "Lewis",
            "Robinson", "Walker", "Hall", "Allen", "Young",
            "King", "Scott", "Green", "Adams", "Baker",
            "Nelson", "Carter", "Mitchell", "Roberts", "Turner"
    };

    private static final String[] FULL_NAMES = generateFullNames();

    private static String[] generateFullNames() {
        String[] fullNames = new String[FIRST_NAMES.length * LAST_NAMES.length];
        int idx = 0;
        for (String first : FIRST_NAMES) {
            for (String last : LAST_NAMES) {
                fullNames[idx++] = first + " " + last;
            }
        }
        return fullNames;
    }

    public enum State {
        IDLE,
        ATTACKING,
        NEUTRAL
    }
}