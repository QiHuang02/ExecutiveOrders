package net.atired.executiveorders.client.renderers.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.atired.executiveorders.enemies.blockentity.VitricCampfireBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class VitricCampfireBlockEntityRenderer implements BlockEntityRenderer<VitricCampfireBlockEntity> {
    private static final Identifier MONOLITH = ExecutiveOrders.id("textures/block/vitric_fire2.png");
    private static final float SCALE = 0.375F;
    private final ItemRenderer itemRenderer;

    public VitricCampfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    public void render(VitricCampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
        DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();
        int k = (int)campfireBlockEntity.getPos().asLong();

        VertexConsumer consumer =  PaleUniformsEvent.createDarkImmediate().getBuffer(RenderLayer.getEntityTranslucent(MONOLITH));

        matrixStack.push();
        matrixStack.translate(0.5F, 0.1F, 0.5F);
        Matrix4f pose = matrixStack.peek().getPositionMatrix();
        MatrixStack.Entry normal = matrixStack.peek();
        long time = campfireBlockEntity.getWorld().getTime()/3;
        vertex(pose,normal,consumer,new Vec3d(-0.5,0,0),1,(time)/8f,-1, 0, 0, 255, 1,1f,1,1);
        vertex(pose,normal,consumer,new Vec3d(0.5,0,0),1,(time+1)/8f,-1, 0, 1, 255, 1,1f,1,1);
        vertex(pose,normal,consumer,new Vec3d(0.4,1.5+Math.sin((campfireBlockEntity.getWorld().getTime()+f)/8f)/3f,0),0,(time+1)/8f,-1, 0, 0, 255, 1,1f,1,1);
        vertex(pose,normal,consumer,new Vec3d(-0.4,1.5+Math.cos((campfireBlockEntity.getWorld().getTime()+f)/8f)/3f,0),0,(time)/8f,-1, 0, 0, 255, 1,1f,1,1);

        vertex(pose,normal,consumer,new Vec3d(0,0,-0.5),1,(time)/8f,-1, 0, 0, 255, 1,1f,1,1);
        vertex(pose,normal,consumer,new Vec3d(0,0,0.5),1,(time+1)/8f,-1, 0, 0, 255, 1,1f,1,1);
        vertex(pose,normal,consumer,new Vec3d(0,1.5+Math.cos((campfireBlockEntity.getWorld().getTime()+f)/8f)/3f,0.4),0,(time+1)/8f,-1, 0, 0, 255, 1,1f,1,1);
        vertex(pose,normal,consumer,new Vec3d(0,1.5+Math.sin((campfireBlockEntity.getWorld().getTime()+f)/8f)/3f,-0.4),0,(time)/8f,-1, 0, 0, 255, 1,1f,1,1);
        matrixStack.translate(-0.5F, -0.1F, -0.5F);
        matrixStack.pop();

        for (int l = 0; l < defaultedList.size(); l++) {
            ItemStack itemStack = defaultedList.get(l);
            if (itemStack != ItemStack.EMPTY) {
                matrixStack.push();
                matrixStack.translate(0.5F, 0.44921875F, 0.5F);
                Direction direction2 = Direction.fromHorizontal((l + direction.getHorizontal()) % 4);
                float g = -direction2.asRotation();
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                matrixStack.translate(-0.3125F, -0.3125F, 0.0F);
                matrixStack.scale(0.375F, 0.375F, 0.375F);
                this.itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, i, j, matrixStack, vertexConsumerProvider, campfireBlockEntity.getWorld(), k + l);
                matrixStack.pop();
            }
        }
    }
    public void vertex(Matrix4f p_254392_, MatrixStack.Entry p_254011_, VertexConsumer p_253902_, Vec3d vec3, float p_254003_, float p_254165_, int p_253982_, int p_254037_, int p_254038_, int p_254271_, float r, float g, float b, float a) {
        p_253902_.vertex(p_254392_, (float)vec3.x, (float)vec3.y, (float)vec3.z).color(r,g,b, a).texture(p_254003_, p_254165_).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(p_254011_, (float)p_253982_, (float)p_254038_, (float)p_254037_);

    }
    public void vertex(Matrix4f p_254392_, MatrixStack.Entry p_254011_, VertexConsumer p_253902_, Vec3d vec3, float p_254003_, float p_254165_, int p_253982_, int p_254037_, int p_254038_, int p_254271_) {
        p_253902_.vertex(p_254392_, (float)vec3.x, (float)vec3.y, (float)vec3.z).color(1f,1f,1f, 1f).texture(p_254003_, p_254165_).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(p_254011_, (float)p_253982_, (float)p_254038_, (float)p_254037_);

    }
}
