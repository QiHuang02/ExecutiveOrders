package net.atired.executiveorders.client.renderers.blockentity;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.atired.executiveorders.enemies.blockentity.BrushableBedrockEntity;
import net.atired.executiveorders.enemies.blockentity.VitricCampfireBlockEntity;
import net.atired.executiveorders.init.EOItemsInit;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.BrushableBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class BrushableBedrockEntityRenderer  implements BlockEntityRenderer<BrushableBedrockEntity> {
    private final ItemRenderer itemRenderer;
    private static final Identifier FIRE = ExecutiveOrders.id("textures/block/flare.png");

    public BrushableBedrockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(BrushableBedrockEntity brushableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (brushableBlockEntity.getWorld() != null) {
            long time = brushableBlockEntity.getWorld().getTime()%1000;
            int k = (Integer)brushableBlockEntity.getCachedState().get(Properties.DUSTED);
            long time2 = brushableBlockEntity.getWorld().getTime()/4%1000;
            if (k > 0) {
                float UVoff =  1 /(float)(4f-k);
                float offset =  (1f/(float)(4f-k))+0.4f;
                matrixStack.push();
                matrixStack.translate(0.5F, 0.5F, 0.5F);
                Matrix4f pose = matrixStack.peek().getPositionMatrix();
                MatrixStack.Entry normal = matrixStack.peek();
                VertexConsumer consumer =  vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(FIRE));

                for (int l = 0; l < 3; l++) {
                    Vec3d rot = new Vec3d(0.5,0,0).multiply(Math.cos(time/9f)/8+1).rotateY((float) (time/40f+Math.sin(time/2f)/12f+l*Math.PI*2/3));

                    time2+=1;
                    vertex(pose,normal,consumer,new Vec3d(0,0,0),(time2)/6f,UVoff,-1, 0, 0, 255, 1,1f,1,1f);
                    vertex(pose,normal,consumer,new Vec3d(rot.x,0,rot.z),(time2+1)/6f,UVoff,-1, 0, 1, 255, 1,1f,1,1f);
                    vertex(pose,normal,consumer,new Vec3d(rot.x,offset+Math.sin((brushableBlockEntity.getWorld().getTime()+f)/16f)/6f,rot.z),(time2+1)/6f,0,-1, 0, 0, 255, 1,1f,1,0.1f);
                    vertex(pose,normal,consumer,new Vec3d(0,offset+Math.cos((brushableBlockEntity.getWorld().getTime()+f)/16f)/6f,0),(time2)/6f,0,-1, 0, 0, 255, 1,1f,1,0.1f);

                }
                for (int l = 0; l < 3; l++) {
                    double v = -time / 10f + Math.sin(time / 8f) / 12f + l * Math.PI * 2 / 3;
                    Vec3d rot = new Vec3d(0.7,0,0).rotateY((float) v);
                    Vec3d rot2 = rot.add(new Vec3d(0.25,0,0).rotateY((float) ( v+Math.PI/2)));
                    rot = rot.add(new Vec3d(0.25,0,0).rotateY((float) ( v-Math.PI/2)));
                    time2+=1;
                    vertex(pose,normal,consumer,new Vec3d(0,0,0),(time2+0.5f)/6f,UVoff,-1, 0, 0, 255, 1,1f,1,1f);
                    vertex(pose,normal,consumer,new Vec3d(0,0,0),(time2+0.5f)/6f,UVoff,-1, 0, 1, 255, 1,1f,1,1f);
                    vertex(pose,normal,consumer,new Vec3d(rot.x,offset+Math.sin((brushableBlockEntity.getWorld().getTime()+f)/16f)/6f,rot.z),(time2+1)/6f,0,-1, 0, 0, 255, 1,1f,1,0.1f);
                    vertex(pose,normal,consumer,new Vec3d(rot2.x,offset+Math.cos((brushableBlockEntity.getWorld().getTime()+f)/16f)/6f,rot2.z),(time2)/6f,0,-1, 0, 0, 255, 1,1f,1,0.1f);

                }
                matrixStack.translate(-0.5F, -0.5F, -0.5F);
                matrixStack.pop();

            }

            if (k > 0) {
                Direction direction = brushableBlockEntity.getHitDirection();
                if (direction != null) {
                    ItemStack itemStack = brushableBlockEntity.getItem();
                    if (!itemStack.isEmpty()) {
                        matrixStack.push();
                        matrixStack.translate(0.0F, 0.5F, 0.0F);

                        float[] fs = this.getTranslation(direction, k);
                        matrixStack.translate(fs[0]+Math.cos((time+f)/15*k)*Math.abs(fs[1])*Math.sin((time+f)/45)/4, fs[1], fs[2]+Math.sin((time+f)/15*k)*Math.abs(fs[1])*Math.cos((time+f)/45*k)/4);
                        matrixStack.multiply(new Quaternionf().rotationYXZ((float) (time+f)/5,(float) Math.cos((time+f)/25)/3,(float) Math.sin((brushableBlockEntity.getWorld().getTime()+f)/25)/3));
                        boolean bl = direction == Direction.EAST || direction == Direction.WEST;
                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)((bl ? 90 : 0) + 11)));
                        matrixStack.scale(0.5F, 0.5F, 0.5F);
                        int l = WorldRenderer.getLightmapCoordinates(
                                brushableBlockEntity.getWorld(), brushableBlockEntity.getCachedState(), brushableBlockEntity.getPos().offset(direction)
                        );
                        if(itemStack.getItem()!= Items.BEDROCK&&itemStack.getItem()!= EOItemsInit.NIGHTMARE_FUEL){
                            this.itemRenderer
                                    .renderItem(
                                            itemStack, ModelTransformationMode.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, brushableBlockEntity.getWorld(), 0
                                    );
                        }
                        else{
                            ItemStack stack = new ItemStack(BrushableBedrockEntity.ITEMS[(int) (time2%BrushableBedrockEntity.ITEMS.length)]);
                            this.itemRenderer
                                    .renderItem(
                                            stack, ModelTransformationMode.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, brushableBlockEntity.getWorld(), 0
                                    );

                        }
                        matrixStack.pop();
                    }
                }
            }
        }
    }
    public void vertex(Matrix4f p_254392_, MatrixStack.Entry p_254011_, VertexConsumer p_253902_, Vec3d vec3, float p_254003_, float p_254165_, int p_253982_, int p_254037_, int p_254038_, int p_254271_, float r, float g, float b, float a) {
        p_253902_.vertex(p_254392_, (float)vec3.x, (float)vec3.y, (float)vec3.z).color(r,g,b, a).texture(p_254003_, p_254165_).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(p_254011_, (float)p_253982_, (float)p_254038_, (float)p_254037_);

    }
    public void vertex(Matrix4f p_254392_, MatrixStack.Entry p_254011_, VertexConsumer p_253902_, Vec3d vec3, float p_254003_, float p_254165_, int p_253982_, int p_254037_, int p_254038_, int p_254271_) {
        p_253902_.vertex(p_254392_, (float)vec3.x, (float)vec3.y, (float)vec3.z).color(1f,1f,1f, 1f).texture(p_254003_, p_254165_).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(p_254011_, (float)p_253982_, (float)p_254038_, (float)p_254037_);

    }
    private float[] getTranslation(Direction direction, int dustedLevel) {
        float[] fs = new float[]{0.5F, 0.0F, 0.5F};
        float f = ((float)Math.pow(dustedLevel,2)) / 10.0F*0.5f;
        switch (direction) {
            case EAST:
                fs[0] = 0.73F + f;
                break;
            case WEST:
                fs[0] = 0.25F - f;
                break;
            case UP:
                fs[1] = 0.25F + f;
                break;
            case DOWN:
                fs[1] = -0.23F - f;
                break;
            case NORTH:
                fs[2] = 0.25F - f;
                break;
            case SOUTH:
                fs[2] = 0.73F + f;
        }

        return fs;
    }

}
