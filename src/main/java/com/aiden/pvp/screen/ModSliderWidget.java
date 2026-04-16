package com.aiden.pvp.screen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class ModSliderWidget extends AbstractSliderButton {
    public ModSliderWidget(int x, int y, int width, int height, Component text, double value) {
        super(x, y, width, height, text, value);
    }

    public abstract void updateMessage();

    public abstract void applyValue();

    public void setValue(double value) {
        double e = this.value;
        this.value = Mth.clamp(value, 0.0, 1.0);
        if (e != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }
}
