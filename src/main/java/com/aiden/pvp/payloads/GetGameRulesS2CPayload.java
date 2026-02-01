package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GetGameRulesS2CPayload(int value1, int value2) implements CustomPacketPayload {
    public static final Identifier GET_GAME_RULES_PAYLOAD_ID = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "get_game_rules_s2c_payload");
    public static final CustomPacketPayload.Type<GetGameRulesS2CPayload> ID = new Type<>(GET_GAME_RULES_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, GetGameRulesS2CPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            GetGameRulesS2CPayload::value1,
            ByteBufCodecs.INT,
            GetGameRulesS2CPayload::value2,
            GetGameRulesS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
