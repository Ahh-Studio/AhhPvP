package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GetGameRulesS2CPayload(int value1, int value2) implements CustomPayload {
    public static final Identifier GET_GAME_RULES_PAYLOAD_ID = Identifier.of(PvP.MOD_ID, "get_game_rules_s2c_payload");
    public static final CustomPayload.Id<GetGameRulesS2CPayload> ID = new Id<>(GET_GAME_RULES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, GetGameRulesS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            GetGameRulesS2CPayload::value1,
            PacketCodecs.INTEGER,
            GetGameRulesS2CPayload::value2,
            GetGameRulesS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
