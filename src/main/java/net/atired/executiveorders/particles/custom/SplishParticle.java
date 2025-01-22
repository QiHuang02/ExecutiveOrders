package net.atired.executiveorders.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SplishParticle extends SpriteBillboardParticle {
    protected SplishParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, double xd, double yd, double zd) {
        super(clientWorld, d, e, f,xd,yd,zd);
        this.x = d;
        this.y = e;
        this.z = f;
        this.velocityX = xd;
        this.velocityY = yd;
        this.velocityZ = zd;
        this.scale = 2f;
        this.maxAge = 9;
        this.setSpriteForAge(spriteProvider);
        this.red = 1;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public void tick() {
        super.tick();

        this.scale += (1-((float) (age+4)/(maxAge+4)))/2;
        this.alpha = 1f-((float) age /maxAge);
        this.red -= (float) Math.pow( (double) age /maxAge,0.5f)/30f;
    }

    protected void method_60373(VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float f) {
        Vec3d vec3d = camera.getPos();
        float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
        float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
        float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
        this.method_60374(vertexConsumer, quaternionf, g, h, i, f);
    }

    protected void method_60374(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i) {
        float j = this.getSize(i);
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(i);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, 1.0f, 0, j, l, n, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, 1.0f, 2.0f, j, l, m, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, -1.0f, 2.0f, j, k, m, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, -1.0f, 0, j, k, n, o);
    }

    private void method_60375(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n) {
        Vector3f vector3f = new Vector3f(i, j, 0.0f).rotate(quaternionf).mul(k).add(f, g, h);
        Vector3f vector3f2 = new Vector3f(i, j, 0.0f).rotate(quaternionf).add(f, g, h);
        vertexConsumer.vertex(vector3f2.x(), vector3f.y(), vector3f2.z()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(n);
    }
    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationYXZ((age+tickDelta)/8f,0,0.0f);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
        quaternionf.rotationYXZ((float)(-Math.PI)+(age+tickDelta)/8f, 0, 0.0f);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
        quaternionf.rotationYXZ((float)(-Math.PI*0.5)+(age+tickDelta)/8f,0,0.0f);
        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
        quaternionf.rotationYXZ((float)(-Math.PI*1.5)+(age+tickDelta)/8f, 0, 0.0f);
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
            return new SplishParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
