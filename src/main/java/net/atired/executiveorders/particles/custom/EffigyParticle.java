package net.atired.executiveorders.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

public class EffigyParticle extends SpriteBillboardParticle {
    protected EffigyParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.x = d;
        this.y = e;
        this.z = f;
        this.velocityX = xd;
        this.velocityY = yd;
        this.velocityZ = zd;
        this.scale = 2f;
        this.maxAge = 30;
        this.setSpriteForAge(spriteProvider);
        this.red = 1;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public void tick() {
        super.tick();
        this.scale += (1-((float) (age+4)/(maxAge+4)))/12;
        this.alpha = 1-((float) age /maxAge);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationYXZ(0,-1.57f,0.0f);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
        quaternionf.rotationYXZ((float)(-Math.PI), 1.57f, 0.0f);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new EffigyParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
