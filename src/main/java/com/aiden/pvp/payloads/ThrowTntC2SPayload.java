package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ThrowTntC2SPayload(int userId) implements CustomPacketPayload {
    public static final Identifier THROW_TNT_PAYLOAD_ID = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "throw_tnt_c2s_payload");
    public static final CustomPacketPayload.Type<ThrowTntC2SPayload> ID = new CustomPacketPayload.Type<>(THROW_TNT_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ThrowTntC2SPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, ThrowTntC2SPayload::userId, ThrowTntC2SPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
