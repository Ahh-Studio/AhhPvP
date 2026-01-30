package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BlockHitC2SPayload(int playerID, boolean isBlocking) implements CustomPayload {
    public static final Id<BlockHitC2SPayload> ID = new Id<>(Identifier.of(PvP.MOD_ID, "block_hit_c2s_payload"));
    public static final PacketCodec<RegistryByteBuf, BlockHitC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            BlockHitC2SPayload::playerID,
            PacketCodecs.BOOLEAN,
            BlockHitC2SPayload::isBlocking,
            BlockHitC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
