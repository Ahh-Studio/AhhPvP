package com.aiden.pvp.explosion;

import com.aiden.pvp.PvP;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class FireballExplosionImpl implements Explosion {
    private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    private final boolean createFire;
    private final Explosion.DestructionType destructionType;
    private final ServerWorld world;
    private final Vec3d pos;
    @Nullable
    private final Entity entity;
    private final float power;
    private final DamageSource damageSource;
    private final ExplosionBehavior behavior;
    private final Map<PlayerEntity, Vec3d> knockbackByPlayer = new HashMap<>();

    public FireballExplosionImpl(ServerWorld world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, Vec3d pos, float power, boolean createFire, DestructionType destructionType) {
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.pos = pos;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? world.getDamageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.makeBehavior(entity) : behavior;
    }

    private ExplosionBehavior makeBehavior(@Nullable Entity entity) {
        return (entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity));
    }

    private void createFire(List<BlockPos> positions) {
        for (BlockPos blockPos : positions) {
            if (this.world.random.nextInt(3) == 0 && this.world.getBlockState(blockPos).isAir() && this.world.getBlockState(blockPos.down()).isOpaqueFullCube()) {
                this.world.setBlockState(blockPos, AbstractFireBlock.getState(this.world, blockPos));
            }
        }
    }

    private void destroyBlocks(List<BlockPos> positions) {
        List<DroppedItem> list = new ArrayList<>();
        Util.shuffle(positions, this.world.random);

        for (BlockPos blockPos : positions) {
            this.world.getBlockState(blockPos).onExploded(this.world, blockPos, this, (item, pos) -> addDroppedItem(list, item, pos));
        }

        for (DroppedItem droppedItem : list) {
            Block.dropStack(this.world, droppedItem.pos, droppedItem.item);
        }
    }

    public int explode() {
        this.getWorld().emitGameEvent(this.getEntity(), GameEvent.EXPLODE, this.getPosition());
        List<BlockPos> list = this.getBlocksToDestroy();
        this.damageEntities();
        if (this.shouldDestroyBlocks()) {
            Profiler profiler = Profilers.get();
            profiler.push("explosion_blocks");
            this.destroyBlocks(list);
            profiler.pop();
        }

        if (this.createFire) {
            this.createFire(list);
        }

        return list.size();
    }

    private void damageEntities() {
        if (!(this.power < 1.0E-5F)) {
            float f = this.power * 2.5F;
            int i = MathHelper.floor(this.pos.x - f - 1.0);
            int j = MathHelper.floor(this.pos.x + f + 1.0);
            int k = MathHelper.floor(this.pos.y - f - 1.0);
            int l = MathHelper.floor(this.pos.y + f + 1.0);
            int m = MathHelper.floor(this.pos.z - f - 1.0);
            int n = MathHelper.floor(this.pos.z + f + 1.0);

            for (Entity entity : this.world.getOtherEntities(this.entity, new Box(i, k, m, j, l, n))) {
                if (!entity.isImmuneToExplosion(this)) {
                    double d = Math.sqrt(entity.squaredDistanceTo(this.pos)) / f; // 距离除以强度的2.5倍
                    if (!(d > 1.0)) { // 距离小于等于强度的2.5倍
                        Vec3d vec3d = entity instanceof TntEntity ? entity.getEntityPos() : entity.getEyePos(); // 实体眼睛的位置
                        Vec3d vec3d2 = vec3d.subtract(this.pos).normalize(); // 位置差（距离向量）
                        boolean bl = this.behavior.shouldDamage(this, entity);
                        float g = this.behavior.getKnockbackModifier(entity);
                        float h = !bl && g == 0.0F ? 0.0F : calculateReceivedDamage(this.pos, entity);
                        if (bl) entity.damage(this.world, this.damageSource, this.behavior.calculateDamage(this, entity, h));

                        double e = entity instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE) : 0.0;
                        double o = (1.0 - d) * h * g * (1.0 - e); // 最终乘数 =  强度的2.5倍减距离 * 伤害 * 击退乘数 * (1.0 - 击退抗性，没有属性值则为0.0)
                        double p = h == 0 ? 0.5 * (1.0 - d) * g * (1.0 - e) : o; // 防止动量为0，修正动量

                        Vec3d vec3d3 = new Vec3d(
                                vec3d2.x * 2,
                                vec3d2.y * 0.5 + 0.1,
                                vec3d2.z * 2
                        );

                        Vec3d vec3d4 = vec3d3.multiply(p);
                        entity.addVelocity(vec3d4);
                        if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
                            projectileEntity.setOwner(this.damageSource.getAttacker());
                        } else if (entity instanceof PlayerEntity playerEntity
                                && !playerEntity.isSpectator()
                                && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.knockbackByPlayer.put(playerEntity, vec3d4);
                        }

                        entity.onExplodedBy(this.entity);
                    }
                }
            }
        }
    }

    private List<BlockPos> getBlocksToDestroy() {
        Set<BlockPos> set = new HashSet<>();
        for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = j / 15.0F * 2.0F - 1.0F;
                        double e = k / 15.0F * 2.0F - 1.0F;
                        double f = l / 15.0F * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.getPower() * (0.7F + this.getWorld().random.nextFloat() * 0.6F);
                        double m = this.getPosition().x;
                        double n = this.getPosition().y;
                        double o = this.getPosition().z;

                        for (; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = BlockPos.ofFloored(m, n, o);
                            BlockState blockState = this.getWorld().getBlockState(blockPos);
                            FluidState fluidState = this.getWorld().getFluidState(blockPos);
                            if (!this.getWorld().isInBuildLimit(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlastResistance(this, this.getWorld(), blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.behavior.canDestroyBlock(this, this.getWorld(), blockPos, blockState, h)) {
                                set.add(blockPos);
                            }

                            m += d * 0.3F;
                            n += e * 0.3F;
                            o += f * 0.3F;
                        }
                    }
                }
            }
        }

        return new ObjectArrayList<>(set);
    }

    public static void createExplosion(
            World world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior,
            double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType,
            ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        ServerWorld serverWorld = (ServerWorld) world;
        Explosion.DestructionType destructionType = switch (explosionSourceType) {
            case NONE -> Explosion.DestructionType.KEEP;
            case BLOCK -> serverWorld.getGameRules().getValue(GameRules.BLOCK_EXPLOSION_DROP_DECAY) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY;
            case MOB -> serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING)
                    ? serverWorld.getGameRules().getValue(GameRules.MOB_EXPLOSION_DROP_DECAY) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY
                    : Explosion.DestructionType.KEEP;
            case TNT -> serverWorld.getGameRules().getValue(GameRules.TNT_EXPLOSION_DROP_DECAY) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY;
            case TRIGGER -> Explosion.DestructionType.TRIGGER_BLOCK;
        };
        Vec3d vec3d = new Vec3d(x, y, z);
        FireballExplosionImpl explosionImpl = new FireballExplosionImpl(serverWorld, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
        int i = explosionImpl.explode();
        ParticleEffect particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

        for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()) {
            if (serverPlayerEntity.squaredDistanceTo(vec3d) < 4096.0) {
                Optional<Vec3d> optional = Optional.ofNullable(explosionImpl.knockbackByPlayer.get(serverPlayerEntity));
                serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(vec3d, power, i, optional, particleEffect, soundEvent, blockParticles));
            }
        }
    }

    @Override
    public ServerWorld getWorld() {
        return this.world;
    }

    @Override
    public DestructionType getDestructionType() {
        return this.destructionType;
    }

    @Override
    public @Nullable LivingEntity getCausingEntity() {
        return Explosion.getCausingEntity(this.entity);
    }

    @Override
    public @Nullable Entity getEntity() {
        return this.entity;
    }

    @Override
    public float getPower() {
        return this.power;
    }

    @Override
    public Vec3d getPosition() {
        return this.pos;
    }

    @Override
    public boolean canTriggerBlocks() {
        if (this.destructionType != Explosion.DestructionType.TRIGGER_BLOCK) {
            return false;
        } else {
            return this.entity != null && this.entity.getType() == EntityType.BREEZE_WIND_CHARGE ? this.world.getGameRules().getValue(GameRules.DO_MOB_GRIEFING) : true;
        }
    }

    @Override
    public boolean preservesDecorativeEntities() {
        boolean bl = this.world.getGameRules().getValue(GameRules.DO_MOB_GRIEFING);
        boolean bl2 = this.entity == null || this.entity.getType() != EntityType.BREEZE_WIND_CHARGE && this.entity.getType() != EntityType.WIND_CHARGE;
        return bl ? bl2 : this.destructionType.destroysBlocks() && bl2;
    }

    private boolean shouldDestroyBlocks() {
        return this.destructionType != Explosion.DestructionType.KEEP;
    }

    public static float calculateReceivedDamage(Vec3d pos, Entity entity) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
            int i = 0;
            int j = 0;

            for (double k = 0.0; k <= 1.0; k += d) {
                for (double l = 0.0; l <= 1.0; l += e) {
                    for (double m = 0.0; m <= 1.0; m += f) {
                        double n = MathHelper.lerp(k, box.minX, box.maxX);
                        double o = MathHelper.lerp(l, box.minY, box.maxY);
                        double p = MathHelper.lerp(m, box.minZ, box.maxZ);
                        Vec3d vec3d = new Vec3d(n + g, o, p + h);
                        if (entity.getEntityWorld()
                                .raycast(new RaycastContext(vec3d, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity))
                                .getType()
                                == HitResult.Type.MISS) {
                            i++;
                        }

                        j++;
                    }
                }
            }

            return (float)i / j;
        } else {
            return 0.0F;
        }
    }

    public boolean isSmall() {
        return this.power < 2.0F || !this.shouldDestroyBlocks();
    }

    private static void addDroppedItem(List<DroppedItem> droppedItemsOut, ItemStack item, BlockPos pos) {
        for (DroppedItem droppedItem : droppedItemsOut) {
            droppedItem.merge(item);
            if (item.isEmpty()) {
                return;
            }
        }

        droppedItemsOut.add(new DroppedItem(pos, item));
    }

    static class DroppedItem {
        final BlockPos pos;
        ItemStack item;

        DroppedItem(BlockPos pos, ItemStack item) {
            this.pos = pos;
            this.item = item;
        }

        public void merge(ItemStack other) {
            if (ItemEntity.canMerge(this.item, other)) {
                this.item = ItemEntity.merge(this.item, other, 16);
            }
        }
    }
}
