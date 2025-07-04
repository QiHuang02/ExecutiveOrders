package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public record WarHornPayload(Vector3f vector3f, float scale) implements CustomPayload {
    public static final Id<WarHornPayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.WARHORN_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, WarHornPayload> CODEC = PacketCodec.tuple(PacketCodecs.VECTOR3F, WarHornPayload::vector3f,PacketCodecs.FLOAT, WarHornPayload::scale, WarHornPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
