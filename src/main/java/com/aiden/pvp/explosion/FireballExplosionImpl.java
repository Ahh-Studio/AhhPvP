package com.aiden.pvp.explosion;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class FireballExplosionImpl implements Explosion {
    private static final ExplosionDamageCalculator DEFAULT_BEHAVIOR = new ExplosionDamageCalculator();
    private final boolean createFire;
    private final Explosion.BlockInteraction destructionType;
    private final ServerLevel world;
    private final Vec3 pos;
    private final Entity entity;
    private final float power;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator behavior;
    private final Map<Player, Vec3> knockbackByPlayer = new HashMap<>();

    public FireballExplosionImpl(ServerLevel world, @Nullable Entity entity, @Nullable DamageSource damageSource,
                                 @Nullable ExplosionDamageCalculator behavior, Vec3 pos, float power,
                                 boolean createFire, BlockInteraction destructionType) {
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.pos = pos;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? world.damageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.makeBehavior(entity) : behavior;
    }

    private ExplosionDamageCalculator makeBehavior(@Nullable Entity entity) {
        return (entity == null ? DEFAULT_BEHAVIOR : new EntityBasedExplosionDamageCalculator(entity));
    }

    private void createFire(List<BlockPos> positions) {
        for (BlockPos blockPos : positions) {
            if (this.world.random.nextInt(3) == 0 && this.world.getBlockState(blockPos).isAir() && this.world.getBlockState(blockPos.below()).isSolidRender()) {
                this.world.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.world, blockPos));
            }
        }
    }

    private void destroyBlocks(List<BlockPos> positions) {
        List<DroppedItem> list = new ArrayList<>();
        Util.shuffle(positions, this.world.random);

        for (BlockPos blockPos : positions) {
            this.world.getBlockState(blockPos).onExplosionHit(this.world, blockPos, this, (item, pos) -> addDroppedItem(list, item, pos));
        }

        for (DroppedItem droppedItem : list) {
            Block.popResource(this.world, droppedItem.pos, droppedItem.item);
        }
    }

    public int explode() {
        this.level().gameEvent(this.getDirectSourceEntity(), GameEvent.EXPLODE, this.center());
        List<BlockPos> list = this.getBlocksToDestroy();
        this.damageEntities();
        if (this.shouldDestroyBlocks()) {
            ProfilerFiller profiler = Profiler.get();
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
            int i = Mth.floor(this.pos.x - f - 1.0);
            int j = Mth.floor(this.pos.x + f + 1.0);
            int k = Mth.floor(this.pos.y - f - 1.0);
            int l = Mth.floor(this.pos.y + f + 1.0);
            int m = Mth.floor(this.pos.z - f - 1.0);
            int n = Mth.floor(this.pos.z + f + 1.0);

            for (net.minecraft.world.entity.Entity entity : this.world.getEntities(this.entity, new AABB(i, k, m, j, l, n))) {
                if (!entity.ignoreExplosion(this)) {
                    double d = Math.sqrt(entity.distanceToSqr(this.pos)) / f; // 距离除以强度的2.5倍
                    if (!(d > 1.0)) { // 距离小于等于强度的2.5倍
                        Vec3 vec3d = entity instanceof PrimedTnt ? entity.position() : entity.getEyePosition(); // 实体眼睛的位置
                        Vec3 vec3d2 = vec3d.subtract(this.pos).normalize(); // 位置差（距离向量）
                        boolean bl = this.behavior.shouldDamageEntity(this, entity);
                        float g = this.behavior.getKnockbackMultiplier(entity);
                        float h = !bl && g == 0.0F ? 0.0F : calculateReceivedDamage(this.pos, entity);
                        if (bl) entity.hurtServer(this.world, this.damageSource, this.behavior.getEntityDamageAmount(this, entity, h));

                        double e = entity instanceof net.minecraft.world.entity.LivingEntity livingEntity ? livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE) : 0.0;
                        double o = (1.0 - d) * h * g * (1.0 - e); // 最终乘数 =  强度的2.5倍减距离 * 伤害 * 击退乘数 * (1.0 - 击退抗性，没有属性值则为0.0)
                        double p = h == 0 ? 0.5 * (1.0 - d) * g * (1.0 - e) : o; // 防止动量为0，修正动量

                        Vec3 vec3d3 = new Vec3(
                                vec3d2.x * 2,
                                vec3d2.y * 0.5 + 0.1,
                                vec3d2.z * 2
                        );

                        Vec3 vec3d4 = vec3d3.scale(p);
                        entity.push(vec3d4);
                        if (entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof Projectile projectileEntity) {
                            projectileEntity.setOwner(this.damageSource.getEntity());
                        } else if (entity instanceof Player playerEntity
                                && !playerEntity.isSpectator()
                                && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.knockbackByPlayer.put(playerEntity, vec3d4);
                        }

                        entity.onExplosionHit(this.entity);
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
                        float h = this.radius() * (0.7F + this.level().random.nextFloat() * 0.6F);
                        double m = this.center().x;
                        double n = this.center().y;
                        double o = this.center().z;

                        for (; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = BlockPos.containing(m, n, o);
                            BlockState blockState = this.level().getBlockState(blockPos);
                            FluidState fluidState = this.level().getFluidState(blockPos);
                            if (!this.level().isInWorldBounds(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlockExplosionResistance(this, this.level(), blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.behavior.shouldBlockExplode(this, this.level(), blockPos, blockState, h)) {
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
            Level world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior,
            double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction explosionSourceType,
            ParticleOptions smallParticle, ParticleOptions largeParticle, WeightedList<ExplosionParticleInfo> blockParticles,
            Holder<SoundEvent> soundEvent
    ) {
        ServerLevel serverWorld = (ServerLevel) world;
        Explosion.BlockInteraction destructionType = switch (explosionSourceType) {
            case NONE -> Explosion.BlockInteraction.KEEP;
            case BLOCK -> serverWorld.getGameRules().get(GameRules.BLOCK_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
            case MOB -> serverWorld.getGameRules().get(GameRules.MOB_GRIEFING)
                    ? serverWorld.getGameRules().get(GameRules.MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY
                    : Explosion.BlockInteraction.KEEP;
            case TNT -> serverWorld.getGameRules().get(GameRules.TNT_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
            case TRIGGER -> Explosion.BlockInteraction.TRIGGER_BLOCK;
        };
        Vec3 vec3d = new Vec3(x, y, z);
        FireballExplosionImpl explosionImpl = new FireballExplosionImpl(serverWorld, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
        int i = explosionImpl.explode();
        ParticleOptions particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

        for (ServerPlayer serverPlayerEntity : serverWorld.players()) {
            if (serverPlayerEntity.distanceToSqr(vec3d) < 4096.0) {
                Optional<Vec3> optional = Optional.ofNullable(explosionImpl.knockbackByPlayer.get(serverPlayerEntity));
                serverPlayerEntity.connection.send(new ClientboundExplodePacket(vec3d, power, i, optional, particleEffect, soundEvent, blockParticles));
            }
        }
    }

    @Override
    public ServerLevel level() {
        return this.world;
    }

    @Override
    public BlockInteraction getBlockInteraction() {
        return this.destructionType;
    }

    @Override
    public @Nullable LivingEntity getIndirectSourceEntity() {
        return Explosion.getIndirectSourceEntity(this.entity);
    }

    @Override
    public @Nullable Entity getDirectSourceEntity() {
        return this.entity;
    }

    @Override
    public float radius() {
        return this.power;
    }

    @Override
    public Vec3 center() {
        return this.pos;
    }

    @Override
    public boolean canTriggerBlocks() {
        if (this.destructionType != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return false;
        } else {
            return this.entity != null && this.entity.getType() == EntityType.BREEZE_WIND_CHARGE ? this.world.getGameRules().get(GameRules.MOB_GRIEFING) : true;
        }
    }

    @Override
    public boolean shouldAffectBlocklikeEntities() {
        boolean bl = this.world.getGameRules().get(GameRules.MOB_GRIEFING);
        boolean bl2 = this.entity == null || this.entity.getType() != EntityType.BREEZE_WIND_CHARGE && this.entity.getType() != EntityType.WIND_CHARGE;
        return bl ? bl2 : this.destructionType.shouldAffectBlocklikeEntities() && bl2;
    }

    private boolean shouldDestroyBlocks() {
        return this.destructionType != Explosion.BlockInteraction.KEEP;
    }

    public static float calculateReceivedDamage(Vec3 pos, net.minecraft.world.entity.Entity entity) {
        AABB box = entity.getBoundingBox();
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
                        double n = Mth.lerp(k, box.minX, box.maxX);
                        double o = Mth.lerp(l, box.minY, box.maxY);
                        double p = Mth.lerp(m, box.minZ, box.maxZ);
                        Vec3 vec3d = new Vec3(n + g, o, p + h);
                        if (entity.level()
                                .clip(new ClipContext(vec3d, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
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
            if (ItemEntity.areMergable(this.item, other)) {
                this.item = ItemEntity.merge(this.item, other, 16);
            }
        }
    }
}
