package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BossSpawnerBlockEntity extends BlockEntity {
    public BossSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOSS_SPAWNER_BLOCK_ENTITY, pos, state);
    }

    public void placeStructure(ServerLevel world) {
        StructureTemplate structureTemplate =  world.getStructureManager().get(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_a1")).orElse(null);
        StructureTemplate structureTemplate2 =  world.getStructureManager().get(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_a2")).orElse(null);
        StructureTemplate structureTemplate3 =  world.getStructureManager().get(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_b1")).orElse(null);
        StructureTemplate structureTemplate4 =  world.getStructureManager().get(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield_b2")).orElse(null);
        if (structureTemplate != null && structureTemplate2 != null && structureTemplate3 != null && structureTemplate4 != null) {
            StructurePlaceSettings structurePlacementSettings = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false)
                    .setKnownShape(true);
            structureTemplate.placeInWorld(
                    world,
                    getBlockPos().offset(new Vec3i(0, -2, 0)),
                    getBlockPos().offset(new Vec3i(0, -2, 0)),
                    structurePlacementSettings,
                    RandomSource.create(Util.getMillis()),
                    Block.UPDATE_CLIENTS
            );
            structureTemplate2.placeInWorld(
                    world,
                    getBlockPos().offset(-25, -2, 0),
                    getBlockPos().offset(-25, -2, 0),
                    structurePlacementSettings,
                    RandomSource.create(Util.getMillis()),
                    Block.UPDATE_CLIENTS
            );
            structureTemplate3.placeInWorld(
                    world,
                    getBlockPos().offset(0, -2, -25),
                    getBlockPos().offset(0, -2, -25),
                    structurePlacementSettings,
                    RandomSource.create(Util.getMillis()),
                    Block.UPDATE_CLIENTS
            );
            structureTemplate4.placeInWorld(
                    world,
                    getBlockPos().offset(-25, -2, -25),
                    getBlockPos().offset(-25, -2, -25),
                    structurePlacementSettings,
                    RandomSource.create(Util.getMillis()),
                    Block.UPDATE_CLIENTS
            );
        }
    }
}
