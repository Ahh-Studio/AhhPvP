package com.aiden.pvp.screen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public abstract class ModSliderWidget extends AbstractSliderButton {
    public ModSliderWidget(int x, int y, int width, int height, Component text, double value) {
        super(x, y, width, height, text, value);
    }

    public abstract void updateMessage();

    public abstract void applyValue();

    @Override
    public void setValue(double value) {
        super.setValue(value);
    }
}
