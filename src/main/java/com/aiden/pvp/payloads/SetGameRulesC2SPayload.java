package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SetGameRulesC2SPayload(int value1, int value2) implements CustomPacketPayload {
    public static final Identifier SET_GAME_RULES_PAYLOAD_ID = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "set_game_rules_c2s_payload");
    public static final CustomPacketPayload.Type<SetGameRulesC2SPayload> ID = new CustomPacketPayload.Type<>(SET_GAME_RULES_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SetGameRulesC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SetGameRulesC2SPayload::value1,
            ByteBufCodecs.INT,
            SetGameRulesC2SPayload::value2,
            SetGameRulesC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
