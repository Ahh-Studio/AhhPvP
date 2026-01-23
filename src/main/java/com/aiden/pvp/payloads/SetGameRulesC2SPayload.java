package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SetGameRulesC2SPayload(int value1, int value2) implements CustomPayload {
    public static final Identifier SET_GAME_RULES_PAYLOAD_ID = Identifier.of(PvP.MOD_ID, "set_game_rules_c2s_payload");
    public static final CustomPayload.Id<SetGameRulesC2SPayload> ID = new CustomPayload.Id<>(SET_GAME_RULES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SetGameRulesC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            SetGameRulesC2SPayload::value1,
            PacketCodecs.INTEGER,
            SetGameRulesC2SPayload::value2,
            SetGameRulesC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
