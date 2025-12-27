package com.aiden.pvp.datagen;

import com.aiden.pvp.PvP;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<AdvancementEntry>> {
    @Override
    public void accept(Consumer<AdvancementEntry> advancementEntryConsumer) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        new ItemStack(Items.FISHING_ROD),
                        Text.translatable("advancements.pvp_mod.root.title"),
                        Text.translatable("advancements.pvp_mod.root.description"),
                        Identifier.of(PvP.MOD_ID, "pvp_mod"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion(
                        "on_tick",
                        TickCriterion.Conditions.createTick()
                )
                .build(advancementEntryConsumer, PvP.MOD_ID + ":pvp_mod/root");
    }
}
