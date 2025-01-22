package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DepthsPayload(int entityID) implements CustomPayload {
    public static final Id<DepthsPayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.DEPTHS_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, DepthsPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, DepthsPayload::entityID, DepthsPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
