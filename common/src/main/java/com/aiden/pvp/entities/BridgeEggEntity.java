package com.aiden.pvp.entities;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.items.ModItems;
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
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

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

        if (this.isRemoved()) return;
        this.applyGravity();
        this.setPosRaw(
                this.getX() + getDeltaMovement().x,
                this.getY() + getDeltaMovement().y,
                this.getZ() + getDeltaMovement().z
        );

        if (!this.level().isClientSide()) {
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        HitResult hitResult = this.pick(getDeltaMovement().length(), 0.0f, false);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        this.spawnContinuousParticles();
        this.placeBlocks();

        this.absSnapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

        if (this.tickCount > 30) this.discard();
    }

    private void spawnContinuousParticles() {
        if (level().isClientSide()) {
            Vec3 pos = this.position();
            spawnParticles(pos, random.nextInt(6) + 10, ParticleTypes.SOUL_FIRE_FLAME);
            spawnParticles(pos, random.nextInt(6) + 10, ParticleTypes.FLAME);
        }
    }

    private void spawnParticles(Vec3 pos, int count, ParticleOptions type) {
        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble(2.0) - 1.0) * this.getBbWidth() * 2;
            double offsetY = (random.nextDouble(2.0) - 1.0) * this.getBbHeight() * 2;
            double offsetZ = (random.nextDouble(2.0) - 1.0) * this.getBbWidth() * 2;

            // 生成粒子
            level().addParticle(
                    type, pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    0.01 * (random.nextDouble() - 0.5),
                    0.01 * (random.nextDouble() - 0.5),
                    0.01 * (random.nextDouble() - 0.5)
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