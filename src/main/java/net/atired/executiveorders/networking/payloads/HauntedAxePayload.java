package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.init.EODataComponentTypeInit;
import net.atired.executiveorders.items.HauntedAxeItem;
import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public record HauntedAxePayload(int value, ItemStack axe) implements CustomPayload {
    public static final Id<HauntedAxePayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.HAUNTED_AXE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, HauntedAxePayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, HauntedAxePayload::value, ItemStack.PACKET_CODEC, HauntedAxePayload::axe, HauntedAxePayload::new);
    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<HauntedAxePayload> {
        @Override
        public void receive(HauntedAxePayload payload, ClientPlayNetworking.Context context) {
            if(payload.axe.getItem() instanceof HauntedAxeItem axeItem){
                payload.axe.set(EODataComponentTypeInit.AXEHEAT,payload.value);
            }
        }


    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
