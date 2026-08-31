package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.ModRegistrationConfig;
import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.function.BiFunction;

public class ModBlockEntityTypes {

    public static BlockEntityType<SlimeBlockEntity> SLIME_BLOCK_ENTITY;
    public static BlockEntityType<BossSpawnerBlockEntity> BOSS_SPAWNER_BLOCK_ENTITY;
    public static BlockEntityType<BossBattleHandlerBlockEntity> BOSS_BATTLE_HANDLER_BLOCK_ENTITY;
    public static BlockEntityType<DefenseTowerBlockEntity> DEFENSE_TOWER_BLOCK_ENTITY;

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
            BiFunction<BlockPos, BlockState, T> factory, Block... validBlocks) {
        try {
            // Use Unsafe to allocate BlockEntityType without calling private constructor
            var unsafeClass = Class.forName("sun.misc.Unsafe");
            var unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            var allocateMethod = unsafeClass.getMethod("allocateInstance", Class.class);
            BlockEntityType<T> type = (BlockEntityType<T>) allocateMethod.invoke(unsafe, BlockEntityType.class);

            // Create a BlockEntitySupplier proxy that delegates to the BiFunction
            // BlockEntitySupplier is package-private, so we use Proxy to create an instance
            Class<?> supplierClass = Class.forName("net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier");
            Object supplier = Proxy.newProxyInstance(
                    supplierClass.getClassLoader(),
                    new Class<?>[]{supplierClass},
                    (proxy, method, args) -> {
                        if ("create".equals(method.getName()) && args.length == 2
                                && args[0] instanceof BlockPos && args[1] instanceof BlockState) {
                            return factory.apply((BlockPos) args[0], (BlockState) args[1]);
                        }
                        return null;
                    }
            );

            // Set the factory field via Unsafe.putObject
            Field factoryField = BlockEntityType.class.getDeclaredField("factory");
            long factoryOffset = (long) unsafeClass.getMethod("objectFieldOffset", Field.class).invoke(unsafe, factoryField);
            unsafeClass.getMethod("putObject", Object.class, long.class, Object.class)
                    .invoke(unsafe, type, factoryOffset, supplier);

            // Set the validBlocks field via reflection
            Field validBlocksField = BlockEntityType.class.getDeclaredField("validBlocks");
            validBlocksField.setAccessible(true);
            validBlocksField.set(type, Set.of(validBlocks));

            // Register the value as an intrusive holder in the registry before registration.
            // Since initialize() is called inside the RegisterEvent callback (NeoForge) or
            // during mod initialization (Fabric), the registry is not frozen in either case.
            MappedRegistry<BlockEntityType<?>> registry = (MappedRegistry<BlockEntityType<?>>) BuiltInRegistries.BLOCK_ENTITY_TYPE;
            registry.createIntrusiveHolder(type);

            return type;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create BlockEntityType", e);
        }
    }

    public static void initialize() {
        try {
            // Always create block entity type objects, even when SKIP_REGISTRY_REGISTER is true
            SLIME_BLOCK_ENTITY = createBlockEntityType(SlimeBlockEntity::new, ModBlocks.SPECIAL_SLIME_BLOCK);
            BOSS_SPAWNER_BLOCK_ENTITY = createBlockEntityType(BossSpawnerBlockEntity::new, ModBlocks.BOSS_SPAWNER);
            BOSS_BATTLE_HANDLER_BLOCK_ENTITY = createBlockEntityType(BossBattleHandlerBlockEntity::new, ModBlocks.BOSS_BATTLE_HANDLER);
            DEFENSE_TOWER_BLOCK_ENTITY = createBlockEntityType(DefenseTowerBlockEntity::new, ModBlocks.DEFENSE_TOWER);

            if (!ModRegistrationConfig.SKIP_REGISTRY_REGISTER) {
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, "slime_block_entity"), SLIME_BLOCK_ENTITY);
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, "boss_spawner_block_entity"), BOSS_SPAWNER_BLOCK_ENTITY);
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, "boss_battle_handler_block_entity"), BOSS_BATTLE_HANDLER_BLOCK_ENTITY);
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(PvP.MOD_ID, "defense_tower_block_entity"), DEFENSE_TOWER_BLOCK_ENTITY);
            }
            PvP.LOGGER.info("[Block Entity Type Initializer] Mod Block Entity Types Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.error("[Block Entity Type Initializer] An Error Occurred: ", e);
        }
    }
}