package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.atired.executiveorders.client.layers.ExecutiveRenderLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer<LivingEntity> {
    private static final Identifier TEXTURE = ExecutiveOrders.id("textures/enchants/execute.png");
    private Framebuffer framebuffer;

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @ModifyReturnValue(method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;", at = @At("RETURN"))
    private @Nullable RenderLayer whySoJelly(@Nullable RenderLayer original,LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline){
        if(entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant())
        {


            Identifier identifier = getTexture(entity);
            RenderLayer layer = ExecutiveRenderLayers.getExecutiveJelly(identifier);
            return layer;
        }


        return original;
    }
    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    private void renderTail(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(livingEntity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant()) {
            RenderSystem.setShaderColor(1, 1, 1, 1f);
        }
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    private void rendermixin(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        this.framebuffer = MinecraftClient.getInstance().getFramebuffer();

        if(livingEntity instanceof LivingEntityAccessor accessor && accessor.getExecuteTime() >0 )
        {

            i = 0xFFFF;
            float timed = (float) (Math.sin((20f-(float)accessor.getExecuteTime()+g)/5.8f)*2*(20-(float)accessor.getExecuteTime()+g)/10f);
            timed*=0.75f;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
            matrixStack.push();
            MatrixStack.Entry entry = matrixStack.peek();
            Quaternionf quaternionf = MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation();
            Vector3f[] $$9 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
            float a = 1;
            if(accessor.getExecuteTime()>15)
            {
                a = ((float)(20-accessor.getExecuteTime()))/5f;
            }
            float scale = 1;
            if(accessor.getExecuteTime()<3)
            {
                scale=1f+(float)(3-accessor.getExecuteTime()+g)/20f;
            }
            for(int $$11 = 0; $$11 < 4; ++$$11) {
                Vector3f $$12 = $$9[$$11];
                $$12.add(0,0,0);
                $$12.rotate(quaternionf);
                $$12.mul(0.5f*scale*livingEntity.getWidth());
                $$12.add(0, livingEntity.getEyeHeight(livingEntity.getPose())+0.8f+timed, 0);
            }

            this.vertex(entry, vertexConsumer, $$9[0].x(), $$9[0].y(), $$9[0].z(), 1.0F, 1.0F, 0, 1, 0, i,a);
            this.vertex(entry, vertexConsumer, $$9[1].x(), $$9[1].y(), $$9[1].z(), 1F, 0.0F, 0, 1, 0, i,a);
            this.vertex(entry, vertexConsumer, $$9[2].x(), $$9[2].y(), $$9[2].z(), 0F, 0F, 0, 1, 0, i,a);
            this.vertex(entry, vertexConsumer, $$9[3].x(), $$9[3].y(), $$9[3].z(), 0.0F, 1F, 0, 1, 0, i,a);
            matrixStack.pop();
        }

    }
    public void vertex(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, int normalX, int normalZ, int normalY, int light,float a) {
        vertexConsumer.vertex(matrix, (float)x, (float)y, (float)z).color(1,1,1,a).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(255,255).normal(matrix, (float)normalX, (float)normalY, (float)normalZ);
    }
}
