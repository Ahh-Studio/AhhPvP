package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GetGameRulesC2SPayload(int playerId) implements CustomPacketPayload {
    public static final ResourceLocation GET_GAME_RULES_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(PvP.MOD_ID, "get_game_rules_c2s_payload");
    public static final CustomPacketPayload.Type<GetGameRulesC2SPayload> ID = new Type<>(GET_GAME_RULES_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, GetGameRulesC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            GetGameRulesC2SPayload::playerId,
            GetGameRulesC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
