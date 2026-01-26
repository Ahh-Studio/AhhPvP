package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishingBobberEntity extends ProjectileEntity {
    private final PositionInterpolator positionInterpolator = new PositionInterpolator(this);
    public boolean shouldDamageStack = false;

    public FishingBobberEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public FishingBobberEntity(World world, PlayerEntity thrower){
        this(ModEntityTypes.FISHING_BOBBER, world);
        this.setOwner(thrower);
        float f = thrower.getPitch();
        float g = thrower.getYaw();
        float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
        float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
        float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
        float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
        double d = thrower.getX() - i * 0.3;
        double e = thrower.getEyeY();
        double l = thrower.getZ() - h * 0.3;
        this.refreshPositionAndAngles(d, e, l, g, f);
        Vec3d vec3d = new Vec3d(-i, MathHelper.clamp(-(k / j), -5.0F, 5.0F), -h);
        double m = vec3d.length();
        vec3d = vec3d.multiply(
                0.6 / m + this.random.nextTriangular(0.5, 0.0103365),
                0.6 / m + this.random.nextTriangular(0.5, 0.0103365),
                0.6 / m + this.random.nextTriangular(0.5, 0.0103365)
        );
        this.setVelocity(vec3d);
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 180.0F / (float)Math.PI));
        this.lastYaw = this.getYaw();
        this.lastPitch = this.getPitch();
    }

    @NotNull
    @Override
    public PositionInterpolator getInterpolator() {
        return this.positionInterpolator;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected boolean deflectsAgainstWorldBorder() {
        return true;
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = 64.0;
        return distance < 4096.0;
    }

    public void tick() {
        this.getInterpolator().tick();
        super.tick();
        PlayerEntity playerOwner = this.getPlayerOwner();
        if (playerOwner == null) this.discard();
        else if (this.getEntityWorld().isClient() || !this.removeIfInvalid(playerOwner)) {
            this.move(MovementType.SELF, this.getVelocity());
            this.tickBlockCollision();
            this.checkForCollision();
            this.updateRotation();
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
            this.setVelocity(this.getVelocity().multiply(0.92));
            this.refreshPosition();
        }
    }

    private boolean removeIfInvalid(PlayerEntity player){
        if (player.isInteractable()) {
            boolean bl1 = player.getMainHandStack().isOf(ModItems.FISHING_ROD);
            boolean bl2 = player.getOffHandStack().isOf(ModItems.FISHING_ROD);
            if ((bl1 || bl2) && this.squaredDistanceTo(player) <= 1024.0) {
                return false;
            }
        }
        this.discard();
        return true;
    }

    private void checkForCollision() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.hitOrDeflect(hitResult);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.setVelocity(this.getVelocity().normalize().multiply(blockHitResult.squaredDistanceTo(this)));
        this.setVelocity(this.getVelocity().multiply(0, 1, 0));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity hitEntity = entityHitResult.getEntity();
        if (hitEntity instanceof LivingEntity livingEntity && getOwner() instanceof LivingEntity livingEntity1 && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            livingEntity.damage(serverWorld, this.getOwner().getDamageSources().mobProjectile(this, livingEntity1), 1);
            this.shouldDamageStack = true;
        }

        this.setVelocity(this.getVelocity().multiply(0, 1, 0));
    }

    private @Nullable ServerWorld getServerWorld() {
        if (getEntityWorld() instanceof ServerWorld serverWorld) {
            return serverWorld;
        } else {
            return null;
        }
    }

    @Nullable
    public PlayerEntity getPlayerOwner() {
        return this.getOwner() instanceof PlayerEntity playerEntity ? playerEntity : null;
    }
}
