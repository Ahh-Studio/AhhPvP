package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BlockHitC2SPayload(int playerID, boolean isBlocking) implements CustomPacketPayload {
    public static final Type<BlockHitC2SPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(PvP.MOD_ID, "block_hit_c2s_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockHitC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            BlockHitC2SPayload::playerID,
            ByteBufCodecs.BOOL,
            BlockHitC2SPayload::isBlocking,
            BlockHitC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
