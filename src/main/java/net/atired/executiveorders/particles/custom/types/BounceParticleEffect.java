package net.atired.executiveorders.particles.custom.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atired.executiveorders.init.ParticlesInit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

import java.util.Locale;

public class BounceParticleEffect implements ParticleEffect {
    protected final Vector3f angle;
    protected final float heat;

    public BounceParticleEffect(Vector3f posTo, float heated){
        this.angle = posTo;
        this.heat = heated;
    }
    public static final MapCodec<BounceParticleEffect> CODEC= RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codecs.VECTOR_3F.fieldOf("angle").forGetter((effect) -> {
            return effect.angle;
        }),Codecs.POSITIVE_FLOAT.fieldOf("heat").forGetter(BounceParticleEffect::getHeat)).apply(instance, BounceParticleEffect::new);
    });
    public static final PacketCodec<RegistryByteBuf, BounceParticleEffect> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VECTOR3F, (effect) -> {
        return ((BounceParticleEffect)effect).angle;
    }, PacketCodecs.FLOAT, BounceParticleEffect::getHeat, BounceParticleEffect::new);;
    public float getHeat(){
        return heat;
    }
    public Vector3f getPosTo() {
        return angle;
    }

    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.angle.x());
        buf.writeFloat(this.angle.y());
        buf.writeFloat(this.angle.z());
    }


    public ParticleType<BounceParticleEffect> getType() {
        return ParticlesInit.HAUNTED_BOUNCE_PARTICLE;
    }

    public String asString() {
        return  String.format(Locale.ROOT, "%s %.2f %.2f %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), this.angle.x(), this.angle.y(), this.angle.z());
    }
}
