package com.aiden.pvp.payloads;

import com.aiden.pvp.PvP;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UpdateInfoToClientPayload(boolean teleportingUsingReturnScroll, int returnScrollTeleportCountDown) implements CustomPacketPayload {
    public static final Identifier UPDATE_INFO_ID = Identifier.fromNamespaceAndPath(PvP.MOD_ID, "update_info_to_client_payload");
    public static final CustomPacketPayload.Type<UpdateInfoToClientPayload> ID = new Type<>(UPDATE_INFO_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateInfoToClientPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            UpdateInfoToClientPayload::teleportingUsingReturnScroll,
            ByteBufCodecs.INT,
            UpdateInfoToClientPayload::returnScrollTeleportCountDown,
            UpdateInfoToClientPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
