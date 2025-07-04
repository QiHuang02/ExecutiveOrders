package net.atired.executiveorders.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class BedrockLeafParticle extends SpriteBillboardParticle {
    protected BedrockLeafParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.x = d;
        this.y = e;
        this.z = f;
        this.red = (float) xd;
        this.green = (float)yd;
        this.blue =(float) zd;
        this.gravityStrength = 0.2f;
        this.scale = 0.3f;
        this.maxAge = 70;
        this.setSpriteForAge(spriteProvider);
        this.velocityY = -0.01f;
        this.angle = (float) ((Math.random()-0.5f)*3.14f*2);
        this.prevAngle = this.angle;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevAngle = this.angle;
        this.angle+=0.1f;
        this.velocityX = Math.sin(this.age/4.0)/16;
        this.velocityZ = Math.cos(this.age/4.0)/16;
        this.alpha = Math.min(2-((float) age /maxAge)*2,1);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        super.buildGeometry(vertexConsumer, camera, tickDelta);


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
            return new BedrockLeafParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
