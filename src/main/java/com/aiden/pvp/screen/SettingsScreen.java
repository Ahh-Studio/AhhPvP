package com.aiden.pvp.screen;

import com.aiden.pvp.payloads.GetGameRulesC2SPayload;
import com.aiden.pvp.payloads.SetGameRulesC2SPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen {
    private Screen parent;
    private int sliderValue1;
    private int sliderValue2;
    private ModSliderWidget sliderWidget1;
    private ModSliderWidget sliderWidget2;

    public SettingsScreen(Screen parent) {
        super(Component.translatable("screen.pvp.settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        if (minecraft.level == null) {
            Button closeButton = Button.builder(
                            Component.literal("Close"),
                            (button) -> this.onClose())
                    .pos(this.width / 2 - 50, this.height / 2 + 50)
                    .size(100, 20)
                    .build();
            this.addRenderableWidget(closeButton);
            return;
        }

        if (minecraft.player != null) {
            GetGameRulesC2SPayload getGameRulesC2SPayload = new GetGameRulesC2SPayload(minecraft.player.getId());
            ClientPlayNetworking.send(getGameRulesC2SPayload);
        }

        sliderWidget1 = new ModSliderWidget(
                width / 4 - 100, height / 4, 200, 20,
                Component.literal("Fireball Explode Power: " + "[???]"), sliderValue1 / 100.0
        ) {
            @Override
            public void updateMessage() {this.setMessage(Component.literal("Fireball Explode Power: " + sliderValue1));}

            @Override
            public void applyValue() {
                sliderValue1 = (int) (this.value * 100);
                if (this.value <= 0) this.value = 0.01;
            }
        };

        sliderWidget2 = new ModSliderWidget(width / 4 * 3 - 100, height / 4, 200, 20,
                Component.literal("Post Hit Damage Immunity: " + "[???]"), sliderValue2 / 10.0) {
            @Override
            public void updateMessage() {this.setMessage(Component.literal("Post Hit Damage Immunity: " + sliderValue2));}

            @Override
            public void applyValue() {
                sliderValue2 = (int) (this.value * 10);
                if (this.value <= 0) this.value = 0.01;
            }
        };
        this.addRenderableWidget(sliderWidget1);
        this.addRenderableWidget(sliderWidget2);

        Button applyButton = Button.builder(
                        Component.literal("Apply"),
                        (button) -> {
                            SetGameRulesC2SPayload payload = new SetGameRulesC2SPayload(sliderValue1, sliderValue2);
                            ClientPlayNetworking.send(payload);
                            this.onClose();
                        })
                .pos(this.width / 2 - 50, this.height / 2 + 50)
                .size(100, 20)
                .build();
        this.addRenderableWidget(applyButton);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawString(this.font, title, 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.setScreen(this.parent);
    }

    public void setSliderValues(int sliderValue1, int sliderValue2) {
        this.sliderValue1 = sliderValue1;
        this.sliderValue2 = sliderValue2;
        if (sliderWidget1 != null && sliderWidget2 != null) {
            // 更新滑块1的value和显示
            sliderWidget1.setValue((double) sliderValue1 / 100);
            sliderWidget1.updateMessage();
            // 更新滑块2的value和显示
            sliderWidget2.setValue((double) sliderValue2 / 10);
            sliderWidget2.updateMessage();
        }
    }
}
