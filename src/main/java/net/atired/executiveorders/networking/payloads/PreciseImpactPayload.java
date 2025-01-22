package net.atired.executiveorders.networking.payloads;

import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public record PreciseImpactPayload(int entityID, double power, Vector3f vec3d,Vector3f velocity) implements CustomPayload {
    public static final Id<PreciseImpactPayload> ID = new Id<>(ExecutiveOrdersNetworkingConstants.PRECISE_IMPACT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, PreciseImpactPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, PreciseImpactPayload::entityID,PacketCodecs.DOUBLE, PreciseImpactPayload::power, PacketCodecs.VECTOR3F, PreciseImpactPayload::vec3d, PacketCodecs.VECTOR3F, PreciseImpactPayload::velocity, PreciseImpactPayload::new);
    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<PreciseImpactPayload> {
        @Override
        public void receive(PreciseImpactPayload payload, ClientPlayNetworking.Context context) {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if (entity instanceof LivingEntity living) {
                Vector3f dist = payload.vec3d().normalize();
                if (entity instanceof LivingEntityAccessor accessor) {
                    accessor.setHollowing(true);
                    entity.addVelocity(new Vec3d(payload.velocity));
                    Vec3d pos = entity.getPos();
                    for (int i = 0; i < 20; i++) {
                        Vec3d vec3d = rotateVectorCC(new Vec3d(1, 0, 0).multiply(payload.power()).rotateZ(i / 10f * 3.14f).rotateY((float) Math.atan2(dist.x, dist.z)), new Vec3d(1, 0, 0).rotateY((float) Math.atan2(dist.x, dist.z)), (float) Math.asin(-dist.y)).add(pos.add(new Vec3d(0, living.getHeight() / 2, 0)));
                        Vector3f editedist = dist;
                        context.client().execute(() ->
                        {
                            context.client().world.addParticle(ParticleTypes.CLOUD, vec3d.x, vec3d.y, vec3d.z, editedist.x, editedist.y, editedist.z);
                        });
                    }
                    for (int i = 0; i < 40; i++) {
                        Vec3d vec3d = rotateVectorCC(new Vec3d(2, 0, 0).multiply(payload.power()).rotateZ(i / 20f * 3.14f).rotateY((float) Math.atan2(dist.x, dist.z)), new Vec3d(1, 0, 0).rotateY((float) Math.atan2(dist.x, dist.z)), (float) Math.asin(-dist.y)).add(pos.add(new Vec3d(0, living.getHeight() / 2, 0)));
                        Vector3f editedist = dist;
                        context.client().execute(() -> {
                            context.client().world.addParticle(ParticleTypes.CLOUD, vec3d.x, vec3d.y, vec3d.z, editedist.x / 4, editedist.y / 4, editedist.z / 4);
                        });
                    }
                }
            }
        }

        public static Vec3d rotateVectorCC(Vec3d vec, Vec3d axis, double theta) {
            double x, y, z;
            double u, v, w;
            x = vec.getX();
            y = vec.getY();
            z = vec.getZ();
            u = axis.getX();
            v = axis.getY();
            w = axis.getZ();
            double xPrime = u * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                    + x * Math.cos(theta)
                    + (-w * y + v * z) * Math.sin(theta);
            double yPrime = v * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                    + y * Math.cos(theta)
                    + (w * x - u * z) * Math.sin(theta);
            double zPrime = w * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                    + z * Math.cos(theta)
                    + (-v * x + u * y) * Math.sin(theta);
            return new Vec3d(xPrime, yPrime, zPrime);
        }
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
