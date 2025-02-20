package net.atired.executiveorders.client.renderers.blockentity;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.blocks.MonolithBlock;
import net.atired.executiveorders.enemies.blockentity.MonolithBlockEntity;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.TransparentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
@Environment(EnvType.CLIENT)
public class MonolithBlockEntityRenderer<T extends MonolithBlockEntity>
        implements BlockEntityRenderer<T> {
    private static final Identifier TEXTURE = ExecutiveOrders.id("textures/block/monolith2.png");

    public MonolithBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float dist = (float) MinecraftClient.getInstance().getCameraEntity().getEyePos().distanceTo(entity.getPos().toCenterPos());
        float scale = 10;
        dist = (10.5f-dist)*2;

        dist = Math.clamp(dist,0,2);
        if(entity.alphaticks>1){
            scale/= (float) Math.pow(entity.alphaticks-tickDelta,0.4f);
            dist/=(entity.alphaticks-tickDelta);
        }

        boolean shouldrender = entity.getCachedState().getBlock() instanceof MonolithBlock monolithBlock && monolithBlock.isPowered(entity.getCachedState());
        if(dist!=0 && shouldrender)
            renderSphere(scale,matrices,vertexConsumers,1,48,new Vec3d(0.5,0.5,0.5),false,dist, entity);
    }

    public void renderSphere(float scale, MatrixStack stack, VertexConsumerProvider mbs, int p_115460_, int amount,Vec3d offset,boolean outline,float transparency, T entity)
    {
        float noiseoff = 10.1f-scale;
        float[][] bonustransparency = new float[amount+1][amount+1];
        Vec3d yeah = new Vec3d(0,0,0);
        Vec3d[][] positions = new Vec3d[amount+1][amount+1];
        float[][] posamounts = new float[amount+1][amount+1];
        for(int i = 0; i < amount+1; i ++)
        {
            for(int j = 0; j < amount; j ++)
            {
                positions[i][j] = yeah;
            }
        }
        Vec3d eyes = MinecraftClient.getInstance().getCameraEntity().getEyePos();
        for (int i = 0; i < amount+1; i++) {
            float angle = 1.57f- (float)i/amount*3.14f;
            float amounter = (float) Math.sin(3.14f*(2+ (double) i /amount));
            for (int j = 0; j < amount; j++) {
                Vec3d offset2 = new Vec3d(scale,0,0).rotateZ(angle).rotateY((float) j/amount*2*3.14f+(entity.getWorld().getTime())/90f);
                offset2 = offset2.multiply(EOgetDatNoise.sampleNoise3D((float)offset2.x,(float)offset2.y+entity.getWorld().getTime()/10f,(float)offset2.z,13f)/70/noiseoff+1f);
                positions[i][j] = offset2.add(0,0.05,0);
                bonustransparency[i][j] = Math.clamp((float) offset2.add(entity.getPos().toCenterPos()).distanceTo(eyes)/0.7f-2.5f,0f,1f);
            }

        }
        stack.translate(offset.x,offset.y,offset.z);
        Matrix4f pose = stack.peek().getPositionMatrix();
        MatrixStack.Entry normal = stack.peek();
        VertexConsumer consumer = mbs.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE,true));
        if(transparency>=1.2f){
            consumer = mbs.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE,true));
        }
        transparency = Math.clamp(transparency,0,1);

        stack.push();
        for(int i = 0; i < amount; i ++)
        {
            for(int j = 0; j < amount; j++)
            {
                vertex(pose,normal,consumer,positions[i][j],((float) j/amount),((float) i/amount), -1, 0, 0, 255, 1,1f,1,transparency*bonustransparency[i][j]);
                vertex(pose,normal,consumer,positions[i+1][j],((float) j/amount),((i+1f)/amount), -1, 0, 0, 255,1,1f,1,transparency*bonustransparency[i+1][j]);
                vertex(pose,normal,consumer,positions[i+1][(j+1)%amount],((j+1f)/amount),((i+1f)/amount), -1, 0, 0, 255,1,1,1,transparency*bonustransparency[i+1][(j+1)%amount]);
                vertex(pose,normal,consumer,positions[i][(j+1)%amount],((j+1f)/amount),((float) i/amount), -1, 0, 0, 255,1,1,1,transparency*bonustransparency[i][(j+1)%amount]);
            }
        }
        stack.pop();
        stack.translate(-offset.x,-offset.y,-offset.z);
    }
    public void vertex(Matrix4f p_254392_, MatrixStack.Entry p_254011_, VertexConsumer p_253902_, Vec3d vec3, float p_254003_, float p_254165_, int p_253982_, int p_254037_, int p_254038_, int p_254271_,float r, float g, float b, float a) {
        p_253902_.vertex(p_254392_, (float)vec3.x, (float)vec3.y, (float)vec3.z).color(r,g,b, a).texture(p_254003_, p_254165_).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(p_254011_, (float)p_253982_, (float)p_254038_, (float)p_254037_);

    }
    public void vertex(Matrix4f p_254392_, MatrixStack.Entry p_254011_, VertexConsumer p_253902_, Vec3d vec3, float p_254003_, float p_254165_, int p_253982_, int p_254037_, int p_254038_, int p_254271_) {
        p_253902_.vertex(p_254392_, (float)vec3.x, (float)vec3.y, (float)vec3.z).color(1f,1f,1f, 1f).texture(p_254003_, p_254165_).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(p_254011_, (float)p_253982_, (float)p_254038_, (float)p_254037_);

    }
}
