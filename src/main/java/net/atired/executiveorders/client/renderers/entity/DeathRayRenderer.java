package net.atired.executiveorders.client.renderers.entity;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.enemies.custom.DeathRayEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class DeathRayRenderer extends EntityRenderer<DeathRayEntity> {
    private static final Identifier MONOLITH = ExecutiveOrders.id("textures/entity/fountain.png");
    private static final Identifier MONOLITH2 = ExecutiveOrders.id("textures/entity/fountain2.png");
    private static final Identifier SPHERE_LOCATION = ExecutiveOrders.id("textures/entity/icosphere.png");


    public DeathRayRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(DeathRayEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(DeathRayEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        float pitch = (float) ((float) (Math.asin(-entity.getDir().getY()))+Math.PI/2);
        float yaw2 = (float) ((float) Math.atan2(entity.getDir().getX(), entity.getDir().getZ())+Math.PI/2);
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(MONOLITH));
        float size = entity.getScale();
        float alpha = Math.clamp(size,0.0f,1.0f);

        for (int i = 0; i < 6; i++) {
            matrices.push();

            matrices.multiply(new Quaternionf().rotationYXZ(yaw2,0,pitch));
            Vec3d pos1 = new Vec3d(2,0,0).rotateY((float) (Math.PI*2*i/6f));
            Vec3d pos2 = new Vec3d(2,0,0).rotateY((float) (Math.PI*2*(i+1)/6f));
            MatrixStack.Entry peek = matrices.peek();
            float time2 = -(entity.getWorld().getTime()%100)/25f;
            float length = (float) entity.getTargetPos().length();

            for (int j = 0; j < Math.ceil(length)/1.5+1; j++) {
                float scale = (float) (1 + Math.sin(time2*12+j*0.5f)/4f)*size*0.75f;
                float scale2 = (float) (1 + Math.sin(time2*12+(j+1)*0.5f)/4f)*size*0.75f;
                float a = alpha * Math.clamp(j/3f-0.3f, 0f, 1f);
                float a2 = alpha * Math.clamp((j+1f)/3f-0.3f, 0f, 1f);

                vertex(peek,consumer, (float) pos1.x*scale, (float) pos1.y+(j)*1.5f,(float) pos1.z*scale,i/3f+time2,time2*6+j*0.33f,0,1,0, 255, a);
                vertex(peek,consumer, (float) pos1.x*scale2, (float) pos1.y+(j+1)*1.5f, (float) pos1.z*scale2,i/3f+time2,time2*6+(1f+j)*0.33f,0,1,0, 255, a2);
                vertex(peek,consumer, (float) pos2.x*scale2, (float) pos2.y+(j+1)*1.5f, (float) pos2.z*scale2,(i+1)/3f+time2,time2*6+(1f+j)*0.33f,0,1, 0,255, a2);
                vertex(peek,consumer,(float) pos2.x*scale, (float) pos2.y+(j)*1.5f, (float) pos2.z*scale,(i+1)/3f+time2,time2*6+j*0.33f,0,1,0,255,a);

            }
            matrices.pop();

        }
        consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(MONOLITH2));
        for (int i = 0; i < 6; i++) {
            matrices.push();
            matrices.multiply(new Quaternionf().rotationYXZ(yaw2, 0, pitch));
            Vec3d pos1 = new Vec3d(2, 0, 0).rotateY((float) (Math.PI * 2 * i / 6f));
            Vec3d pos2 = new Vec3d(2, 0, 0).rotateY((float) (Math.PI * 2 * (i + 1) / 6f));

            MatrixStack.Entry peek = matrices.peek();
            float time2 = -(entity.getWorld().getTime() % 100) / 25f;
            float off1 = (float) Math.sin((i)*2+time2/2*3.14f);
            float off2 = (float) Math.sin((i+1)*2+time2/2*3.14f);
            float length = (float) entity.getTargetPos().length();
            for (int j = 0; j < 2; j++) {
                float scale = (float) Math.pow((1 + j)*size,0.5);
                float scale2 = (float) Math.pow((2 + j)*size,0.5);

                vertex(peek,consumer, (float) pos1.x*scale, (float) (pos1.y+(j)*2-2)+off1,(float) pos1.z*scale,i/2f-time2,(j)*0.5f,0,1,0, 255, 1f);
                vertex(peek,consumer, (float) pos1.x*scale2, (float) (pos1.y+(j+1f)*2-2)+off1, (float) pos1.z*scale2,i/2f-time2,(1f+j)*0.5f,0,1,0, 255, 1f);
                vertex(peek,consumer, (float) pos2.x*scale2, (float) (pos2.y+(j+1f)*2-2)+off2, (float) pos2.z*scale2,(i+1)/2f-time2,(1f+j)*0.5f,0,1, 0,255,1f);
                vertex(peek,consumer,(float) pos2.x*scale, (float) (pos2.y+(j)*2-2)+off2, (float) pos2.z*scale,(i+1)/2f-time2,(j)*0.5f,0,1,0,255,1f);

            }
            matrices.pop();
        }

        VertexConsumer consumer1 = vertexConsumers.getBuffer(ExecutiveOrdersClient.ICOSPHERE_BG.getRenderLayer(RenderLayer.getEntityCutout(SPHERE_LOCATION)));
        matrices.push();

        if(entity.age>2){
            pitch = (float) ((float) (Math.asin(-entity.getDir().getY()))+Math.PI/2);
            yaw2 = (float) ((float) Math.atan2(entity.getDir().getX(), entity.getDir().getZ())+Math.PI/2);
            matrices.multiply(new Quaternionf().rotationYXZ(yaw2,0,pitch));
            MatrixStack.Entry peeky = matrices.peek();
            float scale = 4*alpha;
            vertex(peeky,consumer1, -scale,0,-scale,0,0,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, scale,0,-scale,1,0,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);

            vertex(peeky,consumer1, scale,0,scale,1,1,0,1, 0,255, alpha);
            vertex(peeky,consumer1, 0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, -scale,0,scale,0,1,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);

            vertex(peeky,consumer1, scale,0,scale,1,1,0,1, 0,255, alpha);
            vertex(peeky,consumer1,0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, scale,0,-scale,1,0,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);

            vertex(peeky,consumer1, -scale,0,-scale,0,0,0,1, 0,255, alpha);
            vertex(peeky,consumer1, 0,-scale/2, 0,0.5f,0.5f,0,1,0, 255,alpha);
            vertex(peeky,consumer1, -scale,0,scale,0,1,0,1,0, 255,alpha);
            vertex(peeky,consumer1, 0,-scale/2,0,0.5f,0.5f,0,1,0, 255,alpha);


        }
        matrices.pop();
    }
    public void vertex(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, int normalX, int normalZ, int normalY, int light,float a) {
        vertexConsumer.vertex(matrix, (float)x, (float)y, (float)z).color(1,0.5f,0.5f,a).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, (float)normalX, (float)normalY, (float)normalZ);
    }
    @Override
    public Identifier getTexture(DeathRayEntity entity) {
        return MONOLITH;
    }
}
