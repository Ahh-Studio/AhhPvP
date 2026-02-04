package com.aiden.pvp.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.UseEffects;

import java.util.List;
import java.util.Optional;

public class SwordItem extends Item {
    public SwordItem(Properties settings) {
        super(settings.component(DataComponents.BLOCKS_ATTACKS, new BlocksAttacks(
                0F, 0.0F,
                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0, 0.5F)),
                new BlocksAttacks.ItemDamageFunction(0F, 0F, 0F),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        )).component(DataComponents.USE_EFFECTS, new UseEffects(
                false, true, 0.5F
        )));
    }
}
