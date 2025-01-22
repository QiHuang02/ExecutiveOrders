package net.atired.executiveorders.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class ExecuteParticle extends SpriteBillboardParticle {
    protected ExecuteParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.x = d;
        this.y = e;
        this.z = f;
        this.velocityX = xd;
        this.velocityY = yd;
        this.velocityZ = zd;
        this.scale = 0.5f;
        this.maxAge = 20;
        this.setSpriteForAge(spriteProvider);
        this.red = 1;
        this.green = 0.3f;
        this.blue = 0.3f;
        this.angle = (float) ((Math.random()-0.5f)*3.14f*2);
        this.prevAngle = this.angle;
    }

    @Override
    public void tick() {
        super.tick();
        this.scale += (1-((float) (age+4)/(maxAge+4)))/32;
        this.alpha = 1-((float) age /maxAge);
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
            return new ExecuteParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
