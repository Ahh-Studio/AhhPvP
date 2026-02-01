package com.aiden.pvp.entities;

import java.util.ArrayList;
import java.util.List;

import com.aiden.pvp.items.ModItems;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class DaggerEntity extends Projectile {
    public List<LivingEntity> hitEntities = new ArrayList<>();

    public DaggerEntity(EntityType<? extends DaggerEntity> entityType, Level world) {
        super(ModEntityTypes.DAGGER, world);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity hitEntity =  entityHitResult.getEntity();
        Level entityWorld = entityHitResult.getEntity().level();

        if (hitEntity instanceof LivingEntity hitLivingEntity) {
            if (entityWorld instanceof ServerLevel serverWorld && getOwner() instanceof LivingEntity attacker) {
                if (!hitEntities.contains(hitLivingEntity)) hitLivingEntity.hurtServer(
                        serverWorld,
                        this.getOwner().damageSources().mobProjectile(this, attacker),
                        7.0F
                );
                hitEntities.add(hitLivingEntity);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        BlockState hitBlockState = level().getBlockState(blockHitResult.getBlockPos());
        if (
                hitBlockState.is(Blocks.GLASS)
                        || hitBlockState.is(Blocks.WHITE_STAINED_GLASS)
                        || hitBlockState.is(Blocks.LIGHT_GRAY_STAINED_GLASS)
                        || hitBlockState.is(Blocks.GRAY_STAINED_GLASS)
                        || hitBlockState.is(Blocks.BLACK_STAINED_GLASS)
                        || hitBlockState.is(Blocks.BROWN_STAINED_GLASS)
                        || hitBlockState.is(Blocks.RED_STAINED_GLASS)
                        || hitBlockState.is(Blocks.ORANGE_STAINED_GLASS)
                        || hitBlockState.is(Blocks.YELLOW_STAINED_GLASS)
                        || hitBlockState.is(Blocks.LIME_STAINED_GLASS)
                        || hitBlockState.is(Blocks.GREEN_STAINED_GLASS)
                        || hitBlockState.is(Blocks.CYAN_STAINED_GLASS)
                        || hitBlockState.is(Blocks.LIGHT_BLUE_STAINED_GLASS)
                        || hitBlockState.is(Blocks.BLUE_STAINED_GLASS)
                        || hitBlockState.is(Blocks.PURPLE_STAINED_GLASS)
                        || hitBlockState.is(Blocks.MAGENTA_STAINED_GLASS)
                        || hitBlockState.is(Blocks.PINK_STAINED_GLASS)
                || hitBlockState.is(Blocks.GLASS_PANE)
                        || hitBlockState.is(Blocks.WHITE_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.GRAY_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.BLACK_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.BROWN_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.RED_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.ORANGE_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.YELLOW_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.LIME_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.GREEN_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.CYAN_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.BLUE_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.PURPLE_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.MAGENTA_STAINED_GLASS_PANE)
                        || hitBlockState.is(Blocks.PINK_STAINED_GLASS_PANE)
        ) {
            level().destroyBlock(blockHitResult.getBlockPos(), true, this.getOwner());
            return;
        }
        if (hitBlockState.is(Blocks.SCULK_SHRIEKER)) {
            level().destroyBlock(blockHitResult.getBlockPos(), false, this.getOwner());
            if (level() instanceof ServerLevel serverLevel) {
                ItemEntity itemEntity = new ItemEntity(level(), this.getX(), this.getY(), this.getZ(), new ItemStack(ModItems.BOSS_SPAWNER, 1));
                serverLevel.addFreshEntity(itemEntity);
            }
            return;
        }
        this.discard();
    }

    @Override
    public void tick() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        Vec3 vec3d;
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getLocation();
        } else {
            vec3d = this.position().add(this.getDeltaMovement());
        }

        this.setPos(vec3d);
        this.updateRotation();
        this.applyEffectsFromBlocks();
        super.tick();
        if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitTargetOrDeflectSelf(hitResult);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
    }
}
