package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GetGameRulesC2SPayload(int playerId) implements CustomPayload {
    public static final Identifier GET_GAME_RULES_PAYLOAD_ID = Identifier.of(PvP.MOD_ID, "get_game_rules_c2s_payload");
    public static final CustomPayload.Id<GetGameRulesC2SPayload> ID = new Id<>(GET_GAME_RULES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, GetGameRulesC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            GetGameRulesC2SPayload::playerId,
            GetGameRulesC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
