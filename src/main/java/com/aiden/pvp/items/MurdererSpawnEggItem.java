package com.aiden.pvp.items;

import com.aiden.pvp.entities.ModEntityTypes;
import net.minecraft.world.item.SpawnEggItem;

public class MurdererSpawnEggItem extends SpawnEggItem {
    public MurdererSpawnEggItem(Properties p_43210_) {
        super(p_43210_.spawnEgg(ModEntityTypes.MURDERER.get()));
    }
}
