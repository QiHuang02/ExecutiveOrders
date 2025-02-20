package net.atired.executiveorders.particles.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.atired.executiveorders.client.layers.ExecutiveRenderLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class VoidParticle extends SpriteBillboardParticle {
    protected VoidParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.ascending = true;
        this.gravityStrength = -0.5f;
        this.velocityMultiplier = 0.9f;
        this.x = d;
        this.y = e;
        this.z = f;
        this.velocityX = xd;
        this.velocityY = yd;
        this.velocityZ = zd;
        this.scale = 1f;
        this.maxAge = 50;
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
        this.angle += (1-((float) (age+4)/(maxAge+4)))/16;
        this.scale -= (1-((float) (age+4)/(maxAge+4)))/24;


    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        super.buildGeometry(vertexConsumer, camera, tickDelta);

    }

    @Override
    public ParticleTextureSheet getType() {
        return ExecutiveOrdersClient.PARTICLE_SHEET_VOID;
    }
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new VoidParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
