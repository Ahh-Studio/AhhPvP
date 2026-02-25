package com.aiden.pvp.items;

import com.aiden.pvp.blocks.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class TntBlockItem extends BlockItem {
    public TntBlockItem(Properties settings) {
        super(ModBlocks.TNT.get(), settings);
    }
}
