package com.aiden.pvp.entities;

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

import java.util.ArrayList;
import java.util.List;

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
                        || hitBlockState.is(Blocks.STAINED_GLASS.white())
                        || hitBlockState.is(Blocks.STAINED_GLASS.lightGray())
                        || hitBlockState.is(Blocks.STAINED_GLASS.gray())
                        || hitBlockState.is(Blocks.STAINED_GLASS.black())
                        || hitBlockState.is(Blocks.STAINED_GLASS.brown())
                        || hitBlockState.is(Blocks.STAINED_GLASS.red())
                        || hitBlockState.is(Blocks.STAINED_GLASS.orange())
                        || hitBlockState.is(Blocks.STAINED_GLASS.yellow())
                        || hitBlockState.is(Blocks.STAINED_GLASS.lime())
                        || hitBlockState.is(Blocks.STAINED_GLASS.green())
                        || hitBlockState.is(Blocks.STAINED_GLASS.cyan())
                        || hitBlockState.is(Blocks.STAINED_GLASS.lightBlue())
                        || hitBlockState.is(Blocks.STAINED_GLASS.blue())
                        || hitBlockState.is(Blocks.STAINED_GLASS.purple())
                        || hitBlockState.is(Blocks.STAINED_GLASS.magenta())
                        || hitBlockState.is(Blocks.STAINED_GLASS.pink())
                || hitBlockState.is(Blocks.GLASS_PANE)
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.white())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.lightGray())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.gray())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.black())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.brown())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.red())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.orange())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.yellow())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.lime())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.green())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.cyan())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.lightBlue())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.blue())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.purple())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.magenta())
                        || hitBlockState.is(Blocks.STAINED_GLASS_PANE.pink())
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