package net.atired.executiveorders.init;

import com.mojang.serialization.MapCodec;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.particles.custom.types.BounceParticleEffect;
import net.atired.executiveorders.particles.custom.types.SlashParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EOParticlesInit {
    public static final SimpleParticleType EXECUTE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType VOID_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SMALL_VOID_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType EFFIGY_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SPLISH_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<SlashParticleEffect> HAUNTED_SLASH_PARTICLE = new ParticleType<>(true) {
        @Override
        public MapCodec<SlashParticleEffect> getCodec() {
            return SlashParticleEffect.CODEC;
        }

        @Override
        public PacketCodec<? super RegistryByteBuf, SlashParticleEffect> getPacketCodec() {
            return SlashParticleEffect.PACKET_CODEC;
        }
    };
    public static final ParticleType<BounceParticleEffect> HAUNTED_BOUNCE_PARTICLE = new ParticleType<>(true) {
        @Override
        public MapCodec<BounceParticleEffect> getCodec() {
            return BounceParticleEffect.CODEC;
        }

        @Override
        public PacketCodec<? super RegistryByteBuf, BounceParticleEffect> getPacketCodec() {
            return BounceParticleEffect.PACKET_CODEC;
        }
    };
    public static void  registerParticles()
    {
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("haunted_slash_particle"), HAUNTED_SLASH_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("haunted_bounce_particle"), HAUNTED_BOUNCE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("execute_particle"),EXECUTE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("effigy_particle"),EFFIGY_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("splish_particle"),SPLISH_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("void_particle"),VOID_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("small_void_particle"),SMALL_VOID_PARTICLE);
    }
}
