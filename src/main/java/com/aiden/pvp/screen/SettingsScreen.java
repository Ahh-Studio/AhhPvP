package com.aiden.pvp.screen;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.payloads.SetFBExplodePowerGameruleC2SPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen {
    private Screen parent;
    private int sliderValue1;

    public SettingsScreen(Screen parent) {
        super(Text.translatable("screen.pvp.settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        sliderValue1 = 16;

        if (client.world == null) {
            ButtonWidget closeButton = ButtonWidget.builder(
                            Text.literal("Close"),
                            (button) -> this.close())
                    .position(this.width / 2 - 50, this.height / 2 + 50)
                    .size(100, 20)
                    .build();
            this.addDrawableChild(closeButton);
            return;
        }

        SliderWidget sliderWidget1 = new SliderWidget(
                width / 4 - 100, height / 4, 200, 20,
                Text.literal("Fireball Explode Power: 16"), sliderValue1 / 100.0
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Fireball Explode Power: " + sliderValue1));
            }

            @Override
            protected void applyValue() {
                sliderValue1 = (int) (this.value * 100);
                if (this.value <= 0) this.value = 0.01;
            }
        };
        this.addDrawableChild(sliderWidget1);

        ButtonWidget applyButton = ButtonWidget.builder(
                        Text.literal("Apply"),
                        (button) -> {
                            SetFBExplodePowerGameruleC2SPayload payload = new SetFBExplodePowerGameruleC2SPayload(sliderValue1);
                            ClientPlayNetworking.send(payload);
                            client.setScreen(null);
                        })
                .position(this.width / 2 - 50, this.height / 2 + 50)
                .size(100, 20)
                .build();
        this.addDrawableChild(applyButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawText(this.textRenderer, title, 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public void close() {
        super.close();
        client.setScreen(this.parent);
    }
}
