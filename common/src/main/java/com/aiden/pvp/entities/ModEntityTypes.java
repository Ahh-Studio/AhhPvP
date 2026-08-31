package com.aiden.pvp.entities;

import com.aiden.pvp.ModRegistrationConfig;
import com.aiden.pvp.PvPConstants;
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
    public static EntityType<FireballEntity> FIREBALL;
    public static EntityType<BridgeEggEntity> BRIDGE_EGG;
    public static EntityType<FishingBobberEntity> FISHING_BOBBER;
    public static EntityType<BedBugEntity> BED_BUG;
    public static EntityType<DaggerEntity> DAGGER;
    public static EntityType<MurdererEntity> MURDERER;
    public static EntityType<ChickenDefenseEntity> CHICKEN_DEFENSE;
    public static EntityType<EgglletEntity> EGGLLET;

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> entityType) {
        Identifier identifier = Identifier.fromNamespaceAndPath(PvPConstants.MOD_ID, id);
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, identifier);
        EntityType<T> type = entityType.build(key);
        if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
            return Registry.register(BuiltInRegistries.ENTITY_TYPE, identifier, type);
        }
        return type;
    }

    public static void initialize() {
        try {
            FIREBALL = register(
                    "fireball",
                    EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC)
                            .sized(0.75F, 0.75F)
                            .spawnDimensionsScale(1)
                            .clientTrackingRange(32).updateInterval(5)
                            .ridingOffset(0.5F)
                            .nameTagOffset(0.5F)
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
                    "chicken_defense",
                    EntityType.Builder.of(ChickenDefenseEntity::new, MobCategory.MISC)
                            .sized(0.4F, 0.7F)
                            .eyeHeight(0.644F)
                            .passengerAttachments(new Vec3(0.0, 0.7, -0.1))
                            .clientTrackingRange(10)
            );
            EGGLLET = register(
                    "eggllit",
                    EntityType.Builder.<EgglletEntity>of(EgglletEntity::new, MobCategory.MISC)
                            .noLootTable()
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
            );

            PvPConstants.LOGGER.info("[Entity Initializer] Mod Entities Initialized!");
        } catch (Exception e) {
            PvPConstants.LOGGER.warn("[Entity Initializer] An Error Occurred: {}", e.getMessage());
        }
    }
}