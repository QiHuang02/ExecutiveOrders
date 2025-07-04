package net.atired.executiveorders.client.renderers.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.client.renderers.IcoSphere;
import net.atired.executiveorders.enemies.custom.IcoSphereEntity;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.fabricmc.fabric.mixin.client.rendering.ArmorFeatureRendererMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.Arrays;

public class IcoSphereEntityRenderer extends EntityRenderer<IcoSphereEntity> {
    private static final Identifier SPHERE_LOCATION = ExecutiveOrders.id("textures/entity/icosphere.png");
    private static final Identifier CYLINDER_LOCATION = ExecutiveOrders.id("textures/entity/icocylinder.png");

    private static final Identifier MONOLITH = ExecutiveOrders.id("textures/block/monolith.png");
    public IcoSphereEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(IcoSphereEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        int age = entity.getAge();
        IcoSphere sphere = ExecutiveOrdersClient.SPHERE;
        matrices.translate(0,1.5,0);
        float sinscale = 0;
        if(age%100<50){
            sinscale = (float)  Math.clamp(Math.abs(1f/(age%100-50))*3f,0f,1f);
        }
        else {
            sinscale = (float)  Math.clamp(Math.abs(1f/(age%100-50))*10f,0f,1f);
        }
        float pitch = (float) ((float) (Math.asin(-entity.getDir().getY())));
        float yaw2 = (float) ((float) Math.atan2(entity.getDir().getX(), entity.getDir().getZ()));
        float time = (entity.getWorld().getTime()%8000)/800f;
        float alpha = 1f;
        if(entity.age<9){
            alpha = (entity.age+tickDelta+1)/10f;
            alpha = (float) Math.pow(alpha,2f);
        }
        if(entity.age>480){
            alpha = (500-(entity.age+tickDelta+1))/20f;
            alpha = (float) Math.pow(alpha,2f);
        }

        VertexConsumer consumer1 = vertexConsumers.getBuffer(ExecutiveOrdersClient.ICOSPHERE_BG.getRenderLayer(RenderLayer.getEntityTranslucent(SPHERE_LOCATION)));
        matrices.push();
        if(entity.age>20){
            matrices.multiply(new Quaternionf().set(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation()));
            MatrixStack.Entry peeky = matrices.peek();
            float scale = 4*alpha;
            vertex(peeky,consumer1, -scale,-scale,0,0,0,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, scale,-scale,0,1,0,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);

            vertex(peeky,consumer1, scale,scale,0,1,1,0,1, 0,255, alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, -scale,scale,0,0,1,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);

            vertex(peeky,consumer1, scale,scale,0,1,1,0,1, 0,255, alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, scale,-scale,0,1,0,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);

            vertex(peeky,consumer1, -scale,-scale,0,0,0,0,1, 0,255, alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, -scale,scale,0,0,1,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,0,-6,0.5f,0.5f,0,1,0, 255,alpha);
            matrices.pop();
            consumer1 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(MONOLITH));
            matrices.push();
            matrices.multiply(new Quaternionf().set(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation()));
            peeky = matrices.peek();
            vertex(peeky,consumer1, -1.5f,-1.5f,0,0,0,0,1,0, 255,1);
            vertex(peeky,consumer1, 1.5f,-1.5f,0,1,0,0,1,0, 255,1);
            vertex(peeky,consumer1, 1.5f,1.5f,0,1,1,0,1, 0,255, 1f);
            vertex(peeky,consumer1, -1.5f,1.5f,0,0,1,0,1,0, 255,1);
        }

        matrices.pop();
        for (int i = 0; i < sphere.index.size(); i+=3) {
            Vec3d one = sphere.pos.get(sphere.index.get(i));
            Vec3d two = sphere.pos.get(sphere.index.get(i+1));
            Vec3d three = sphere.pos.get(sphere.index.get(i+2));
            double oneYaw= Math.atan2(one.getX(), one.getZ());
            oneYaw =Math.sin(oneYaw*16+time*6.28f*10)/4;
            double twoYaw= Math.atan2(two.getX(), two.getZ());
            twoYaw = Math.sin(twoYaw*16+time*6.28f*10)/4;
            double threeYaw= Math.atan2(three.getX(), three.getZ());
            threeYaw = Math.sin(threeYaw*16+time*6.28f*10)/4;
            Vec3d one2 = one.rotateY(-time*20).rotateX(0.4f).multiply(2+oneYaw);
            Vec3d two2 = two.rotateY(-time*20).rotateX(0.4f).multiply(2+twoYaw);
            Vec3d three2 =three.rotateY(-time*20).rotateX(0.4f).multiply(2+threeYaw);


            Vec2f vec2f = sphere.uvs.get(sphere.index.get(i+2)).add(sphere.uvs.get(sphere.index.get(i+1)).multiply(-1)).multiply(-0.1f);
                float[] v = new float[]{(float) sphere.uvs.get(sphere.index.get(i)).add(vec2f).x/11f, (float) sphere.uvs.get(sphere.index.get(i+1)).x/11f, (float) sphere.uvs.get(sphere.index.get(i+2)).x/11f,(float) sphere.uvs.get(sphere.index.get(i)).add(vec2f.multiply(-1)).x/11f};
                float[] u = new float[]{(float) sphere.uvs.get(sphere.index.get(i)).add(vec2f).y/3f, (float) sphere.uvs.get(sphere.index.get(i+1)).y/3f, (float) sphere.uvs.get(sphere.index.get(i+2)).y/3f,(float) sphere.uvs.get(sphere.index.get(i)).add(vec2f.multiply(-1)).y/3f};

            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(SPHERE_LOCATION));
                matrices.push();
                MatrixStack.Entry peek = matrices.peek();
                one = one.rotateY(time*20).rotateX(0.4f);
                two = two.rotateY(time*20).rotateX(0.4f);
                three = three.rotateY(time*20).rotateX(0.4f);
                double n1 = Math.clamp(2-one.distanceTo(entity.getDir()),0.33f,2f)*sinscale;
                double n2 =   Math.clamp(2-two.distanceTo(entity.getDir()),0.33f,2f)*sinscale;
                double n3 =    Math.clamp(2-three.distanceTo(entity.getDir()),0.33f,2f)*sinscale;

                Vec3d center = one.lerp(two,0.5).lerp(three,0.5).multiply(2).multiply(Math.max(threeYaw,0)).multiply((1/Math.pow(alpha,2)));
                three = three.multiply(4.1+threeYaw+ Math.pow(n3*4-2f*sinscale,3)/32).multiply(0.75).add(center).multiply(alpha);
                two = two.multiply(4+twoYaw+  Math.pow(n2*4-2f*sinscale,3)/32).multiply(0.75).add(center).multiply(alpha);
                one = one.multiply(4+oneYaw+ Math.pow(n1*4-2f*sinscale,3)/32+(float)Math.pow(n3/2f,4f)*sinscale*2).multiply(0.75).add(center).multiply(alpha);

                if((float)Math.pow(n3/2f,4f)*sinscale<0.2f){
                    vertex(peek,consumer, (float) one.getX(),(float)one.getY(),(float)one.getZ(),v[0],u[0],0,1,0, (int) (Math.pow(n1-.33f,4)*255),alpha);
                    vertex(peek,consumer, (float) two.getX(),(float)two.getY(),(float)two.getZ(),v[1],u[1],0,1,0, (int) (Math.pow(n2-.33f,4)*255),alpha);
                    vertex(peek,consumer, (float) three.getX(),(float)three.getY(),(float)three.getZ(),v[2],u[2],0,1, 0,(int) (Math.pow(n3-.33f,4)*255), (1f- (float)Math.pow(n3/2f,4f)*sinscale)*alpha);
                    vertex(peek,consumer, (float) one.getX(),(float)one.getY(),(float)one.getZ(),v[3],u[3],0,1,0, (int) (Math.pow(n1-.33f,4)*255),alpha);
                }
                matrices.pop();
        }

        if(sinscale>0.4f){
            pitch = (float) ((float) (Math.asin(-entity.getDir().getY()))+Math.PI/2);
             yaw2 = (float) ((float) Math.atan2(entity.getDir().getX(), entity.getDir().getZ())+Math.PI/2);
            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(CYLINDER_LOCATION));
            for (int i = 0; i < 6; i++) {
                matrices.push();

                matrices.multiply(new Quaternionf().rotationYXZ(yaw2,0,pitch));
                Vec3d pos1 = new Vec3d(2,0,0).rotateY((float) (Math.PI*2*i/6f));
                Vec3d pos2 = new Vec3d(2,0,0).rotateY((float) (Math.PI*2*(i+1)/6f));
                MatrixStack.Entry peek = matrices.peek();
                float time2 = -(entity.getWorld().getTime()%100)/25f;
                vertex(peek,consumer, 0, (float) pos1.y,0,i/6f,time2,0,1,0, 255,(sinscale-0.4f)/3f*5f);
                vertex(peek,consumer, (float) pos1.x*2, (float) pos1.y+3, (float) pos1.z*2,i/6f,time2+1f,0,1,0, 255,0);
                vertex(peek,consumer, (float) pos2.x*2, (float) pos2.y+3, (float) pos2.z*2,(i+1)/6f,time2+1f,0,1, 0,255, 0);
                vertex(peek,consumer,0, (float) pos2.y, 0,(i+1)/6f,time2,0,1,0,255,(sinscale-0.4f)/3f*5f);
                matrices.pop();
            }
        }
        matrices.translate(0,-1.5,0);



    }
    public void vertex(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, int normalX, int normalZ, int normalY, int light,float a) {
        vertexConsumer.vertex(matrix, (float)x, (float)y, (float)z).color(1,1,1,a).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, (float)normalX, (float)normalY, (float)normalZ);
    }
    @Override
    public Identifier getTexture(IcoSphereEntity entity) {
        return SPHERE_LOCATION;
    }
}
