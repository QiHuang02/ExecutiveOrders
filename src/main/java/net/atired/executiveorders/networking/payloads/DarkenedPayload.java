package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DarkenedPayload(int entityID) implements CustomPayload {
    public static final Id<DarkenedPayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.EXECUTE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, DarkenedPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, DarkenedPayload::entityID, DarkenedPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
