package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SetFBExplodePowerGameruleC2SPayload(int value) implements CustomPayload {
    public static final Identifier SET_GAMERULES_PAYLOAD_ID = Identifier.of(PvP.MOD_ID, "set_gamerules_c2s_payload");
    public static final CustomPayload.Id<SetFBExplodePowerGameruleC2SPayload> ID = new CustomPayload.Id<>(SET_GAMERULES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SetFBExplodePowerGameruleC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SetFBExplodePowerGameruleC2SPayload::value, SetFBExplodePowerGameruleC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
