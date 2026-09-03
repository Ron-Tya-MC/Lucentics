package io.github.rontyamc.lucentics.network;

import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MillingProcessPayload(BlockPos pos, int processingContinue, int processingTimeMax) implements CustomPacketPayload {
    public static final Type<MillingProcessPayload> TYPE = new Type<>(Lucentics.defaultLocation("milling_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MillingProcessPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, MillingProcessPayload::pos,
            ByteBufCodecs.VAR_INT, MillingProcessPayload::processingContinue,
            ByteBufCodecs.VAR_INT, MillingProcessPayload::processingTimeMax,
            MillingProcessPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
