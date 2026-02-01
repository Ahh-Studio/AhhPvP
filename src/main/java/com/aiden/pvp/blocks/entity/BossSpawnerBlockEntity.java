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
        StructureTemplate structureTemplate =  world.getStructureManager().get(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "battlefield")).orElse(null);
        if (structureTemplate != null) {
            StructurePlaceSettings structurePlacementData = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false)
                    .setKnownShape(true);
            structureTemplate.placeInWorld(
                    world,
                    getBlockPos().offset(new Vec3i(-17, -2, -17)),
                    getBlockPos().offset(new Vec3i(-17, -2, -17)),
                    structurePlacementData,
                    RandomSource.create(Util.getMillis()),
                    Block.UPDATE_CLIENTS
            );
        }
    }
}
