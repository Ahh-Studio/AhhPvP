package com.aiden.pvp.items;

import com.aiden.pvp.blocks.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ThrowableTntBlockItem extends BlockItem {
    public ThrowableTntBlockItem(Properties settings) {
        super(ModBlocks.THROWABLE_TNT.get(), settings);
    }
}
