package net.atired.executiveorders.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SandedParticle extends SpriteBillboardParticle {
    protected SandedParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd,BlockState state) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.x = d;
        this.y = e;
        this.z = f;
        this.velocityX = xd;
        this.velocityY = yd+0.5;
        this.velocityZ = zd;
        this.scale = 0.3f;
        this.maxAge = 20;
        this.setSpriteForAge(spriteProvider);
        this.red = 1F;
        this.green = 1F;
        this.blue = 1f;

        int i = MinecraftClient.getInstance().getBlockColors().getParticleColor(state, world, new BlockPos((int) Math.floor(d), (int) Math.floor(e), (int) Math.floor(f)));

        this.red *= (float)(i >> 16 & 0xFF) / 255.0F;
        this.green *= (float)(i >> 8 & 0xFF) / 255.0F;
        this.blue *= (float)(i & 0xFF) / 255.0F;

        this.angle = (float) ((Math.random()-0.5f)*3.14f*2);
        this.prevAngle = this.angle;
    }

    @Override
    public void tick() {
        super.tick();
        this.scale += (1-((float) (age+4)/(maxAge+4)))/64;
        this.alpha = 1-((float) age /maxAge);
        this.velocityY -= 0.08;
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
    public static class Factory implements ParticleFactory<BlockStateParticleEffect> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        @Nullable
        public Particle createParticle(
                BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
        ) {
            BlockState blockState = blockStateParticleEffect.getBlockState();
            return !blockState.isAir() && !blockState.isOf(Blocks.MOVING_PISTON) && blockState.hasBlockBreakParticles()
                    ? new SandedParticle(clientWorld, d, e, f,sprites, g, h, i, blockState)
                    : null;
        }
    }

}
