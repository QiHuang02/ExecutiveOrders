package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record ExecutePayload(int entityID) implements CustomPayload {
    public static final CustomPayload.Id<ExecutePayload> ID = new CustomPayload.Id<>(ExecutiveOrdersNetworkingConstants.EXECUTE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ExecutePayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, ExecutePayload::entityID, ExecutePayload::new);


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
