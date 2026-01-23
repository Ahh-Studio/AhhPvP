package com.aiden.pvp.screen;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public abstract class ModSliderWidget extends SliderWidget {
    public ModSliderWidget(int x, int y, int width, int height, Text text, double value) {
        super(x, y, width, height, text, value);
    }

    public abstract void updateMessage();

    public abstract void applyValue();

    @Override
    public void setValue(double value) {
        super.setValue(value);
    }
}
