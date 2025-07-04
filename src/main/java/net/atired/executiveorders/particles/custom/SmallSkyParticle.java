
package net.atired.executiveorders.particles.custom;

import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class SmallSkyParticle extends SpriteBillboardParticle {
    protected SmallSkyParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.ascending = false;
        this.gravityStrength = .33f;
        this.velocityMultiplier = 0.9f;
        this.x = d;
        this.y = e;
        this.z = f;
        this.velocityX = xd;
        this.velocityY = yd+0.5;
        this.velocityZ = zd;
        this.scale = 1.5f+(float)Math.random()/4f;
        this.maxAge = 20;
        this.setSpriteForAge(spriteProvider);
        this.red = 1;
        this.green = 1f;
        this.blue =1f;
        this.angle = (float) ((Math.random()-0.5f)*3.14f*2);
        this.prevAngle = this.angle;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevAngle = this.angle;
        this.angle += (1-((float) (age+4)/(maxAge+4)))/9;
        this.scale -= (1-((float) (age+4)/(maxAge+4)))/5f;
        this.alpha -= (1-((float) (age+4)/(maxAge+4)))/12;
        this.scale = Math.max(this.scale,0);

    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        super.buildGeometry(vertexConsumer, camera, tickDelta);

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
            return new SmallSkyParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
