package com.aiden.pvp.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class MurdererEntity extends HostileEntity {
    public MurdererEntity(EntityType<? extends MurdererEntity> type, World world) {
        super(ModEntities.MURDERER, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
        this.goalSelector.add(4, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
    }

    public static DefaultAttributeContainer.Builder createMurdererAttributes() {
        return new DefaultAttributeContainer.Builder()
                .add(EntityAttributes.MAX_HEALTH, 40)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE)
                .add(EntityAttributes.MOVEMENT_SPEED)
                .add(EntityAttributes.ARMOR, 300)
                .add(EntityAttributes.ARMOR_TOUGHNESS)
                .add(EntityAttributes.MAX_ABSORPTION)
                .add(EntityAttributes.STEP_HEIGHT)
                .add(EntityAttributes.SCALE)
                .add(EntityAttributes.GRAVITY)
                .add(EntityAttributes.SAFE_FALL_DISTANCE)
                .add(EntityAttributes.FALL_DAMAGE_MULTIPLIER)
                .add(EntityAttributes.JUMP_STRENGTH)
                .add(EntityAttributes.OXYGEN_BONUS)
                .add(EntityAttributes.BURNING_TIME)
                .add(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY)
                .add(EntityAttributes.MOVEMENT_EFFICIENCY)
                .add(EntityAttributes.ATTACK_KNOCKBACK)
                .add(EntityAttributes.CAMERA_DISTANCE)
                .add(EntityAttributes.WAYPOINT_TRANSMIT_RANGE)
                .add(EntityAttributes.FOLLOW_RANGE, 20.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 15.0)
                .add(EntityAttributes.ATTACK_SPEED, 10);
    }

    public State getState() {
        return MurdererEntity.State.CROSSED;
    }

    public enum State {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL;
    }
}
