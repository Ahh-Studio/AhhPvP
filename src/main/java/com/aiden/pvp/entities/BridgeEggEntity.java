package com.aiden.pvp.entities;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.items.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BridgeEggEntity extends ThrowableItemProjectile {
    private final Random random = new Random();
    public BridgeEggEntity(EntityType<? extends BridgeEggEntity> entityType, Level world) {
        super(entityType, world);
        this.setGlowingTag(true);
    }

    public BridgeEggEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntityTypes.BRIDGE_EGG, owner, world, stack);
    }

    public BridgeEggEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(ModEntityTypes.BRIDGE_EGG, x, y, z, world, stack);
    }



    @Override
    protected Item getDefaultItem() {
        return ModItems.BRIDGE_EGG;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) hitResult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
    }
    @Override
    protected void onInsideBlock(BlockState state) {
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
    }

    @Override
    public void tick() {
        // 先判断世界是否为null（避免空指针）
        if (this.level() == null) {
            PvP.LOGGER.warn("BlockEggEntity实体所在世界为null！");
            return;
        }

        // 1. 处理实体生命周期（如是否已移除）
        if (this.isRemoved()) return;

        // 3. 应用重力（复刻ProjectileEntity的重力逻辑，而非EggEntity的）
        this.applyGravity();

        // 4. 更新位置（根据速度移动）
        this.setPosRaw(
                this.getX() + getDeltaMovement().x,
                this.getY() + getDeltaMovement().y,
                this.getZ() + getDeltaMovement().z
        );

        if (!this.level().isClientSide()) { // 仅服务器端执行
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        // 5. 处理碰撞检测（手动调用碰撞逻辑，替代父类的处理）
        HitResult hitResult = this.pick(getDeltaMovement().length(), 0.0f, false);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult); // 触发自定义碰撞处理
        }

        this.spawnContinuousParticles();
        this.placeBlocks();

        // 6. 同步客户端和服务器的位置（必要的网络同步）
        this.absSnapTo(
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getYRot(),   // 实体当前偏航角
                this.getXRot()  // 实体当前俯仰角
        );

        if (this.tickCount > 30) this.discard();
    }

    // 持续生成粒子的核心方法
    private void spawnContinuousParticles() {
        if (level().isClientSide()) {
            // 只在客户端生成粒子（优化性能，服务器无需处理）
            Vec3 pos = this.position(); // 获取实体当前位置

            // 每次生成1种粒子，10-15个
            spawnParticles(pos, random.nextInt(6) + 10, ParticleTypes.SOUL_FIRE_FLAME);
            spawnParticles(pos, random.nextInt(6) + 10, ParticleTypes.FLAME);
        }
    }

    private void spawnParticles(Vec3 pos, int count, ParticleOptions type) {
        for (int i = 0; i < count; i++) {
            // 计算随机偏移量（围绕实体分布）
            double offsetX = (random.nextDouble(2.0) - 1.0) * this.getBbWidth() * 2;
            double offsetY = (random.nextDouble(2.0) - 1.0) * this.getBbHeight() * 2;
            double offsetZ = (random.nextDouble(2.0) - 1.0) * this.getBbWidth() * 2;

            // 生成粒子
            level().addParticle(
                    type,
                    pos.x + offsetX,   // 粒子X坐标
                    pos.y + offsetY,   // 粒子Y坐标
                    pos.z + offsetZ,   // 粒子Z坐标
                    0.01 * (random.nextDouble() - 0.5), // 微小X方向速度
                    0.01 * (random.nextDouble() - 0.5), // 微小Y方向速度
                    0.01 * (random.nextDouble() - 0.5)  // 微小Z方向速度
            );
        }
    }

    private void placeBlocks() {
        if (this.hasNoOtherEntitiesAround(1.2)) {
            if (level().getBlockState(this.blockPos(0, -2, 0)).is(Blocks.AIR) || level().getBlockState(this.blockPos(0, -2, 0)).is(Blocks.CAVE_AIR)) placeBlocks(0, -2, 0);
            if (level().getBlockState(this.blockPos(1, -2, 0)).is(Blocks.AIR) || level().getBlockState(this.blockPos(1, -2, 0)).is(Blocks.CAVE_AIR)) placeBlocks(1, -2, 0);
            if (level().getBlockState(this.blockPos(-1, -2, 0)).is(Blocks.AIR) || level().getBlockState(this.blockPos(-1, -2, 0)).is(Blocks.CAVE_AIR)) placeBlocks(-1, -2, 0);
            if (level().getBlockState(this.blockPos(0, -2, 1)).is(Blocks.AIR) || level().getBlockState(this.blockPos(0, -2, 1)).is(Blocks.CAVE_AIR)) placeBlocks(0, -2, 1);
            if (level().getBlockState(this.blockPos(0, -2, -1)).is(Blocks.AIR) || level().getBlockState(this.blockPos(0, -2, -1)).is(Blocks.CAVE_AIR)) placeBlocks(0, -2, -1);
        }
    }

    private void placeBlocks(int i, int j, int k) {
        level().setBlock(
                this.blockPos(i, j, k),
                ModBlocks.EGG_BRIDGE.defaultBlockState(),
                6
        );
    }

    private @NotNull BlockPos blockPos(int i, int j, int k) {
        return new BlockPos(
                this.blockPosition().getX()+i,
                this.blockPosition().getY()+j,
                this.blockPosition().getZ()+k
        );
    }

    private boolean hasNoOtherEntitiesAround(double radius) {
        AABB box = new AABB(
                this.getX() - radius, this.getY()-2 - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius
        );
        return this.level().getEntities(
                this,  // 排除的实体（自身）
                box,   // 检测范围
                entity -> entity instanceof LivingEntity  // 其他筛选条件
        ).isEmpty();
    }

    public BridgeEggEntity getThis() {
        return this;
    }
}
