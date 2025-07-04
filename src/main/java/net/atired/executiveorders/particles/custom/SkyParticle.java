package net.atired.executiveorders.particles.custom;

import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.joml.Quaternionf;

public class SkyParticle extends SpriteBillboardParticle {
    private SpriteProvider provider;
    private float bonusscale;
    protected SkyParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.x = d;
        this.y = e;
        this.z = f;


        this.bonusscale = (float) new Vec3d(xd,yd,zd).length()+1;
        this.scale = 0.5f+ this.bonusscale/8f;
        this.maxAge = 15;
        this.setSpriteForAge(spriteProvider);
        this.provider = spriteProvider;
        this.red = 1;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public void tick() {
        super.tick();
        this.scale += (1-((float) (age+4)/(maxAge+4)))/4* this.bonusscale;
        this.alpha = 1-((float) age /maxAge);
        setSpriteForAge(this.provider);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationYXZ(0,-1.57f,(age+tickDelta)/32f+this.bonusscale);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
        quaternionf.rotationYXZ((float)(-Math.PI), 1.57f, (age+tickDelta)/32f+this.bonusscale);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ExecutiveOrdersClient.PARTICLE_SHEET_SKY;
    }
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new SkyParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
