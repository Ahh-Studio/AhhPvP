package com.aiden.pvp.payloads.handler;

import com.aiden.pvp.payloads.GetGameRulesS2CPayload;
import com.aiden.pvp.screen.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

public class ClientPayloadHandler {
    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(GetGameRulesS2CPayload.TYPE, (payload, context) -> {
            context.enqueueWork(() -> {
                if (Minecraft.getInstance().screen instanceof SettingsScreen settingsScreen) {
                    settingsScreen.setSliderValues(
                            payload.value1(),
                            payload.value2()
                    );
                }
            });
        });
    }
}
