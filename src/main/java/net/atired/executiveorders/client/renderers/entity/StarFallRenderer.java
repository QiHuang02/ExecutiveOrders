package net.atired.executiveorders.client.renderers.entity;


import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enemies.custom.StarFallEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;




@Environment(EnvType.CLIENT)
public class StarFallRenderer extends EntityRenderer<StarFallEntity> {
    public StarFallRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = 1;
    }

    @Override
    public boolean shouldRender(StarFallEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

    @Override
    public Identifier getTexture(StarFallEntity entity) {
        return TRAIL_TEXTURE;
    }

    private static final Identifier TRAIL_TEXTURE = ExecutiveOrders.id("textures/particle/star.png");
    private final float scale;
    private static final float MIN_CAMERA_DISTANCE_SQUARED = 90.25F;
    private final ItemRenderer itemRenderer;
    private int time;



    public void render(StarFallEntity pEntity, float yaw, float ticks, MatrixStack pose, VertexConsumerProvider bufferSource, int packedLight) {
            if (pEntity.hasTrail()) {
                double x = MathHelper.lerp(ticks, pEntity.prevX, pEntity.getX());
                double y = MathHelper.lerp(ticks, pEntity.prevY, pEntity.getY());
                double z = MathHelper.lerp(ticks, pEntity.prevZ, pEntity.getZ());
                pose.push();
                pose.translate(-x, -y, -z);
                renderTrail(pEntity, ticks, pose, bufferSource, 1F,1F,0.9F, 0.3F, 250);
                pose.pop();
            }
        super.render(pEntity, yaw, ticks, pose, bufferSource, packedLight);
    }


    private void renderTrail(StarFallEntity entityIn, float partialTicks, MatrixStack poseStack, VertexConsumerProvider bufferIn, float trailR, float trailG, float trailB, float trailA, int packedLightIn) {

        int sampleSize = 10;
        float trailHeight = 0.8F;
        float trailZRot = entityIn.age;
        float sideoffset = (float) Math.sin(entityIn.age)/10f;
        float oldoffset = 0;
        if(entityIn.getRandumb()<0.03)
        {
            trailHeight+=0.2f*(entityIn.getRandumb()+0.05f)*70;
            trailR = 0.7f;
            trailB = 0.8f;
        }
        Vec3d drawFrom = entityIn.getTrailPosition(0, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(TRAIL_TEXTURE));

        float yRot = -MinecraftClient.getInstance().getCameraEntity().getYaw(0)%360f/180f*3.14f;



        for(int samples = 0; samples < 10; samples++) {
            float randumb = (float)(Math.random()-0.5f)/4;
            Vec3d topAngleVec = new Vec3d(0, trailHeight, 0).rotateZ(0.5f*3.14f+randumb).rotateY(yRot);
            Vec3d bottomAngleVec = new Vec3d(0, -trailHeight, 0).rotateZ(0.5f*3.14f+randumb).rotateY(yRot);
            Vec3d sample = entityIn.getTrailPosition(samples + 2, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;
            float dist = 25/MinecraftClient.getInstance().getCameraEntity().distanceTo(entityIn);
            Vec3d draw1 = drawFrom;
            Vec3d draw2 = sample;

            MatrixStack.Entry posestack$pose = poseStack.peek();
            Matrix4f matrix4f = posestack$pose.getPositionMatrix();
            Matrix3f matrix3f = posestack$pose.getNormalMatrix();

            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x+ (float) topAngleVec.multiply(oldoffset).x, (float) draw1.y + (float) bottomAngleVec.y+ (float) topAngleVec.multiply(oldoffset).y, (float) draw1.z + (float) bottomAngleVec.z+ (float) topAngleVec.multiply(oldoffset).z).color(trailR, trailG, trailB, MathHelper.clamp(trailA*dist,0f,1f)).texture(u1, 1F).overlay(OverlayTexture.DEFAULT_UV).light(packedLightIn).normal(0.0F, 1.0F, 0.0F);
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) bottomAngleVec.x+ (float) topAngleVec.multiply(sideoffset).x, (float) draw2.y + (float) bottomAngleVec.y+ (float) topAngleVec.multiply(sideoffset).y, (float) draw2.z + (float) bottomAngleVec.z+ (float) topAngleVec.multiply(sideoffset).z).color(trailR, trailG, trailB,  MathHelper.clamp(trailA*dist,0f,1f)).texture(u2, 1F).overlay(OverlayTexture.DEFAULT_UV).light(packedLightIn).normal(0.0F, 1.0F, 0.0F);
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) topAngleVec.x+ (float) topAngleVec.multiply(sideoffset).x, (float) draw2.y + (float) topAngleVec.y+ (float) topAngleVec.multiply(sideoffset).y, (float) draw2.z + (float) topAngleVec.z+ (float) topAngleVec.multiply(sideoffset).z).color(trailR, trailG, trailB,  MathHelper.clamp(trailA*dist,0f,1f)).texture(u2, 0).overlay(OverlayTexture.DEFAULT_UV).light(packedLightIn).normal(0.0F, 1.0F, 0.0F);
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x + (float) topAngleVec.multiply(oldoffset).x, (float) draw1.y + (float) topAngleVec.y+ (float) topAngleVec.multiply(oldoffset).y, (float) draw1.z + (float) topAngleVec.z+ (float) topAngleVec.multiply(oldoffset).z).color(trailR, trailG, trailB,  MathHelper.clamp(trailA*dist,0f,1f)).texture(u1, 0).overlay(OverlayTexture.DEFAULT_UV).light(packedLightIn).normal(0.0F, 1.0F, 0.0F);
            drawFrom = sample;

            sideoffset = 0;
            trailA+=0.07f;
            trailB+=0.01f;
            trailR-=0.04f;
            trailG-=0.09f;


        }
    }


}
