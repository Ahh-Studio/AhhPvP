package com.aiden.pvp.entities.murderer;

import com.aiden.pvp.entities.DaggerEntity;
import com.aiden.pvp.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 长时间打不到目标时的策略：
 * 朝目标方向挖掘阻挡的方块（挖掘距离与玩家一致，客户端显示挖掘裂缝，手上临时拿对应工具）
 * 并周期性射出飞刀远程攻击。
 */
public class StuckMiningAttackGoal extends Goal {
    /** 挖掘交互距离，与玩家的方块交互距离一致 */
    private static final double MINE_REACH = 4.5;
    /** 超过该tick仍无进展，判定为"长时间打不到目标" */
    private static final long STUCK_THRESHOLD = 20;
    /** 视为已进入近战范围的距离平方 */
    private static final float MELEE_DIST_SQ = 16.0F;
    /** 挖掘时可用的"下界合金/剪刀"工具候选，按方块选择最快的 */
    private static final Item[] TOOLS = {
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_AXE,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_SWORD,
            Items.SHEARS
    };

    private final MurdererEntity mob;
    private final ItemStack defaultMainHand = new ItemStack(ModItems.THROWABLE_DAGGER);
    private long lastProgressTime;
    private BlockPos miningPos;
    private float breakProgress;
    private boolean toolEquipped;
    private int rangedCooldown;

    public StuckMiningAttackGoal(MurdererEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.level().isClientSide()) return false;
        // 由 CombatManager 统一决策：仅当当前动作是破墙挖掘才激活
        return this.mob.combatManager.isStuckMining();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        super.start();
        this.lastProgressTime = this.mob.level().getGameTime();
        this.rangedCooldown = 20;
    }

    @Override
    public void stop() {
        super.stop();
        this.miningPos = null;
        this.restoreMainHand();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            this.restoreMainHand();
            return;
        }

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // 重新看到目标或已进入近战范围：视为有进展，重置卡住计时
        if (this.mob.hasLineOfSight(target) || this.mob.distanceToSqr(target) <= MELEE_DIST_SQ) {
            this.lastProgressTime = this.mob.level().getGameTime();
            return;
        }

        if (this.mob.level().getGameTime() - this.lastProgressTime < STUCK_THRESHOLD) {
            return;
        }

        if (this.mob.level() instanceof ServerLevel serverLevel) {
            // 边挖边以低速向目标推进，避免"站桩挖"，保持对玩家的压迫感
            this.mob.getNavigation().moveTo(target, 0.3);

            this.processMining(serverLevel, target);

            this.rangedCooldown--;
            if (this.rangedCooldown <= 0) {
                this.throwDagger(serverLevel, target);
                this.rangedCooldown = 40;
            }
        }
    }

    /**
     * 朝目标方向挖掘阻挡的方块。挖掘距离与玩家一致，客户端通过破坏进度显示裂缝，
     * 挖掘速度等同于玩家手持对应下界合金/剪刀工具的速度。
     */
    private void processMining(ServerLevel level, LivingEntity target) {
        if (this.miningPos == null) {
            BlockHitResult hit = this.raycastBlock(level, target);
            if (hit.getType() != HitResult.Type.BLOCK) {
                // 前方没有要挖的方块，本次不挖
                this.restoreMainHand();
                return;
            }
            BlockPos pos = hit.getBlockPos();
            BlockState initial = level.getBlockState(pos);
            if (this.isUnbreakable(level, pos, initial)) {
                this.restoreMainHand();
                return;
            }
            this.miningPos = pos;
            this.breakProgress = 0.0F;
            // 把对应的下界合金/剪刀工具临时切换到手上
            this.mob.setItemSlot(EquipmentSlot.MAINHAND, this.pickTool(initial));
            this.toolEquipped = true;
        }

        BlockState state = level.getBlockState(this.miningPos);
        if (state.isAir() || this.isUnbreakable(level, this.miningPos, state)) {
            this.miningPos = null;
            this.restoreMainHand();
            return;
        }

        float hardness = state.getDestroySpeed(level, this.miningPos);
        ItemStack held = this.mob.getMainHandItem();
        float toolSpeed = held.getDestroySpeed(state);
        if (hardness <= 0.0F) {
            // 可瞬间破坏的方块直接挖掉
            level.destroyBlock(this.miningPos, true);
            level.destroyBlockProgress(this.mob.getId(), this.miningPos, -1);
            this.miningPos = null;
            this.restoreMainHand();
            return;
        }

        // 每个tick的挖掘进度 = 工具速度 / (硬度 × 30或100)，与玩家计算一致
        boolean correct = held.isCorrectToolForDrops(state);
        float divisor = correct ? 30.0F : 100.0F;
        float progress = toolSpeed / (hardness * divisor);
        this.breakProgress += progress;

        int stage = Mth.clamp((int) (this.breakProgress * 10.0F), 0, 9);
        level.destroyBlockProgress(this.mob.getId(), this.miningPos, stage);

        if (this.breakProgress >= 1.0F) {
            level.destroyBlock(this.miningPos, true);
            level.destroyBlockProgress(this.mob.getId(), this.miningPos, -1);
            this.miningPos = null;
            this.restoreMainHand();
        }
    }

    private void throwDagger(ServerLevel level, LivingEntity target) {
        Vec3 dir = target.getEyePosition().subtract(this.mob.getEyePosition()).normalize();
        DaggerEntity daggerEntity = DaggerAttackGoal.throwDaggerAt(this.mob, dir);

        this.mob.lookAt(target, 30.0F, 30.0F);
        this.mob.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F);
        level.addFreshEntity(daggerEntity);
    }

    private BlockHitResult raycastBlock(ServerLevel level, LivingEntity target) {
        Vec3 eyePos = this.mob.getEyePosition();
        Vec3 toTarget = target.getEyePosition().subtract(eyePos);
        if (toTarget.lengthSqr() < 0.0001) {
            toTarget = new Vec3(0, -1, 0);
        }
        Vec3 endPos = eyePos.add(toTarget.normalize().scale(MINE_REACH));
        return level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
    }

    private ItemStack pickTool(BlockState state) {
        ItemStack best = new ItemStack(Items.NETHERITE_PICKAXE);
        float bestSpeed = 1.0F;
        for (Item item : TOOLS) {
            ItemStack candidate = new ItemStack(item);
            float speed = candidate.getDestroySpeed(state);
            if (speed > bestSpeed) {
                best = candidate;
                bestSpeed = speed;
            }
        }
        return best;
    }

    private boolean isUnbreakable(ServerLevel level, BlockPos pos, BlockState state) {
        return state.is(Blocks.BEDROCK)
                || state.is(Blocks.OBSIDIAN)
                || state.getDestroySpeed(level, pos) < 0.0F;
    }

    private void restoreMainHand() {
        if (this.toolEquipped) {
            this.mob.setItemSlot(EquipmentSlot.MAINHAND, this.defaultMainHand);
            this.toolEquipped = false;
        }
    }
}