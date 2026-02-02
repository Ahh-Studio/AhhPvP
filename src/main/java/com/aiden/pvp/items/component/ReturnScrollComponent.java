package com.aiden.pvp.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ReturnScrollComponent(boolean teleporting, int teleportTime) {
    public static final Codec<ReturnScrollComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.fieldOf("teleporting").forGetter(ReturnScrollComponent::teleporting),
            Codec.INT.fieldOf("teleportTime").forGetter(ReturnScrollComponent::teleportTime)
    ).apply(builder, ReturnScrollComponent::new));
}
