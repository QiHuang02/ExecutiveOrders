package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ArbalestPayload(int entityID, float scale) implements CustomPayload {
    public static final Id<ArbalestPayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.ARBALEST_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ArbalestPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, ArbalestPayload::entityID,PacketCodecs.FLOAT,ArbalestPayload::scale, ArbalestPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
