package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishingBobberEntity extends Projectile {
    private final InterpolationHandler positionInterpolator = new InterpolationHandler(this);
    public boolean shouldDamageStack = false;

    public FishingBobberEntity(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    public FishingBobberEntity(Level world, Player thrower){
        this(ModEntityTypes.FISHING_BOBBER, world);
        this.setOwner(thrower);
        float f = thrower.getXRot();
        float g = thrower.getYRot();
        float h = Mth.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
        float i = Mth.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
        float j = -Mth.cos(-f * (float) (Math.PI / 180.0));
        float k = Mth.sin(-f * (float) (Math.PI / 180.0));
        double d = thrower.getX() - i * 0.3;
        double e = thrower.getEyeY();
        double l = thrower.getZ() - h * 0.3;
        this.snapTo(d, e, l, g, f);
        Vec3 vec3d = new Vec3(-i, Mth.clamp(-(k / j), -5.0F, 5.0F), -h);
        double m = vec3d.length();
        vec3d = vec3d.multiply(
                0.6 / m + this.random.triangle(0.5, 0.0103365),
                0.6 / m + this.random.triangle(0.5, 0.0103365),
                0.6 / m + this.random.triangle(0.5, 0.0103365)
        );
        this.setDeltaMovement(vec3d);
        this.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
        this.setXRot((float)(Mth.atan2(vec3d.y, vec3d.horizontalDistance()) * 180.0F / (float)Math.PI));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @NotNull
    @Override
    public InterpolationHandler getInterpolation() {
        return this.positionInterpolator;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected boolean shouldBounceOnWorldBorder() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 64.0;
        return distance < 4096.0;
    }

    @Override
    public void tick() {
        this.getInterpolation().interpolate();
        super.tick();
        Player playerOwner = this.getPlayerOwner();
        if (playerOwner == null) this.discard();
        else if (this.level().isClientSide() || !this.removeIfInvalid(playerOwner)) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            this.checkForCollision();
            this.updateRotation();
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
            this.reapplyPosition();
        }
    }

    private boolean removeIfInvalid(Player player){
        if (player.canInteractWithLevel()) {
            boolean bl1 = player.getMainHandItem().is(ModItems.FISHING_ROD);
            boolean bl2 = player.getOffhandItem().is(ModItems.FISHING_ROD);
            if ((bl1 || bl2) && this.distanceToSqr(player) <= 1024.0) {
                return false;
            }
        }
        this.discard();
        return true;
    }

    private void checkForCollision() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        this.hitTargetOrDeflectSelf(hitResult);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(blockHitResult.distanceTo(this)));
        this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity hitEntity = entityHitResult.getEntity();
        if (hitEntity instanceof LivingEntity livingEntity && getOwner() instanceof LivingEntity livingEntity1 && this.level() instanceof ServerLevel serverWorld) {
            livingEntity.hurtServer(serverWorld, this.getOwner().damageSources().mobProjectile(this, livingEntity1), 1);
            this.shouldDamageStack = true;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
    }

    private @Nullable ServerLevel getServerWorld() {
        if (level() instanceof ServerLevel serverWorld) {
            return serverWorld;
        } else {
            return null;
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        return this.getOwner() instanceof Player playerEntity ? playerEntity : null;
    }
}
