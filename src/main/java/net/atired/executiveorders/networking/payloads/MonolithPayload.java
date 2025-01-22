package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record MonolithPayload(int x,int y, int z, int value) implements CustomPayload {
    public static final Id<MonolithPayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.MONOLITH_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, MonolithPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, MonolithPayload::x,PacketCodecs.INTEGER, MonolithPayload::y,PacketCodecs.INTEGER, MonolithPayload::z,PacketCodecs.INTEGER, MonolithPayload::value, MonolithPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
