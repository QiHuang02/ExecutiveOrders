package net.atired.executiveorders.init;

import com.mojang.serialization.MapCodec;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.particles.custom.types.BounceParticleEffect;
import net.atired.executiveorders.particles.custom.types.SlashParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

public class EOParticlesInit {
    public static final SimpleParticleType EXECUTE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType VOID_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SMALL_VOID_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SKY_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SMALL_SKY_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType EFFIGY_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SPLISH_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType BEDROCKLEAF_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<BlockStateParticleEffect> SANDED_PARTICLE = new ParticleType<>(true) {
        @Override
        public MapCodec<BlockStateParticleEffect> getCodec() {
            return ((Function<ParticleType<BlockStateParticleEffect>, MapCodec<BlockStateParticleEffect>>)BlockStateParticleEffect::createCodec).apply(this);
        }

        @Override
        public PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect> getPacketCodec() {
            return  ((Function<ParticleType<BlockStateParticleEffect>, PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect>>)BlockStateParticleEffect::createPacketCodec).apply(this);
        }
    };
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
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("sky_particle"),SKY_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("small_sky_particle"),SMALL_SKY_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("sanded_particle"),SANDED_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, ExecutiveOrders.id("bedrockleaf"),BEDROCKLEAF_PARTICLE);
    }
}
