package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public class EODataComponentTypeInit {
    public static final ComponentType<Integer> AXEHEAT = register("axeheat", (builder) -> {
        return builder.codec(Codecs.rangedInt(0, 200)).packetCodec(PacketCodecs.VAR_INT);
    });
    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {

        return (ComponentType) Registry.register(Registries.DATA_COMPONENT_TYPE, ExecutiveOrders.id(id), ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
    }
    public static void init(){

    }
}
