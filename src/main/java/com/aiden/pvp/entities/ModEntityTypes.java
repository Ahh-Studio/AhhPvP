package com.aiden.pvp.entities;

import com.aiden.pvp.PvP;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;

public class ModEntityTypes {
    public static final EntityType<FireballEntity> FIREBALL;
    public static final EntityType<BridgeEggEntity> BRIDGE_EGG;
    public static final EntityType<FishingBobberEntity> FISHING_BOBBER;
    public static final EntityType<BedBugEntity> BED_BUG;
    public static final EntityType<DaggerEntity> DAGGER;
    public static final EntityType<MurdererEntity> MURDERER;
    public static final EntityType<ChickenDefenseEntity> CHICKEN_DEFENSE;
    public static final EntityType<EggllitEntity> EGGLLIT;

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(PvP.MOD_ID, id),
                entityType.build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, id))));
    }

    public static void initialize() {
        try {
            FabricDefaultAttributeRegistry.register(MURDERER, MurdererEntity.createMurdererAttributes().build());
            FabricDefaultAttributeRegistry.register(CHICKEN_DEFENSE, ChickenDefenseEntity.createAttributes().build());
            PvP.LOGGER.info("[Entity Initializer] Mod Entities Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Entity Initializer] An Error Occurred: {}", e.getMessage());
        }
    }

    static {
        FIREBALL = register(
                "fireball",
                EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC)
                        .sized(0.75F, 0.75F)
                        .spawnDimensionsScale(1)
                        .clientTrackingRange(32).updateInterval(5)
                        .ridingOffset(0.5F)
                        .nameTagOffset(0.5F)
                        .alwaysUpdateVelocity(true)
                        .noLootTable()
                        .eyeHeight(0.5F)
        );
        BRIDGE_EGG = register(
                "bridge_egg",
                EntityType.Builder.<BridgeEggEntity>of(BridgeEggEntity::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F)
                        .spawnDimensionsScale(1)
                        .clientTrackingRange(32).updateInterval(5)
                        .ridingOffset(0.01F)
                        .nameTagOffset(0.01F)
                        .noLootTable()
                        .alwaysUpdateVelocity(true)
                        .eyeHeight(0.15F)
        );
        FISHING_BOBBER = register(
                "fishing_bobber",
                EntityType.Builder.<FishingBobberEntity>of(FishingBobberEntity::new, MobCategory.MISC)
                        .noLootTable()
                        .noSave()
                        .noSummon()
                        .sized(0.25F, 0.25F)
                        .clientTrackingRange(4)
                        .updateInterval(5)
        );
        BED_BUG = register(
                "bed_bug",
                EntityType.Builder.<BedBugEntity>of(BedBugEntity::new, MobCategory.MISC)
                        .noLootTable()
                        .sized(0.25F, 0.25F)
                        .clientTrackingRange(4)
                        .updateInterval(10)
        );
        DAGGER = register(
                "dagger",
                EntityType.Builder.of(DaggerEntity::new, MobCategory.MISC)
                        .noLootTable()
                        .noSave()
                        .noSummon()
                        .sized(0.5F, 0.5F)
                        .clientTrackingRange(4)
                        .updateInterval(5)
        );
        MURDERER = register(
                "murderer",
                EntityType.Builder.of(MurdererEntity::new, MobCategory.MONSTER)
                        .sized(0.6F, 1.95F)
                        .clientTrackingRange(8)
                        .notInPeaceful()
        );
        CHICKEN_DEFENSE = register(
                "chicken",
                EntityType.Builder.of(ChickenDefenseEntity::new, MobCategory.MISC)
                        .sized(0.4F, 0.7F)
                        .eyeHeight(0.644F)
                        .passengerAttachments(new Vec3(0.0, 0.7, -0.1))
                        .clientTrackingRange(10)
        );
        EGGLLIT = register(
                "eggllit",
                EntityType.Builder.<EggllitEntity>of(EggllitEntity::new, MobCategory.MISC)
                        .noLootTable()
                        .sized(0.25F, 0.25F)
                        .clientTrackingRange(4)
                        .updateInterval(10)
        );
    }
}
