package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ThrowTntC2SPayload(int userId) implements CustomPayload {
    public static final Identifier THROW_TNT_PAYLOAD_ID = Identifier.of(PvP.MOD_ID, "throw_tnt_c2s_payload");
    public static final CustomPayload.Id<ThrowTntC2SPayload> ID = new CustomPayload.Id<>(THROW_TNT_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, ThrowTntC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, ThrowTntC2SPayload::userId, ThrowTntC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
