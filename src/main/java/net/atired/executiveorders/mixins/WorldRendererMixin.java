package net.atired.executiveorders.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow private @Nullable VertexBuffer lightSkyBuffer;
    @Shadow private @Nullable ClientWorld world;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void captureFrustum();

    @Shadow protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Shadow protected abstract boolean canDrawEntityOutlines();

    @Shadow @Final private BufferBuilderStorage bufferBuilders;
    @Unique
    private final List<Entity> entityList = new ArrayList<Entity>();
    private static final Identifier NETHER_SKY = ExecutiveOrders.id("textures/misc/monolith.png");
    private static final Identifier NETHER_MAW = ExecutiveOrders.id("textures/misc/maw1.png");
    private static final Identifier NETHER_MAW2 = ExecutiveOrders.id("textures/misc/maw2.png");
    private static final Identifier FOG_SKY = ExecutiveOrders.id("textures/misc/ruhroh.png");
    private static final Identifier FOGGIEST_SKY = ExecutiveOrders.id("textures/misc/ruhroh_2.png");
    private static final Identifier FOGGIER_SKY = ExecutiveOrders.id("textures/misc/ruhroh_1.png");
    @Inject(method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",at = @At("RETURN"),cancellable = true)
    private void notRenderSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix(matrix4f);
            renderNetherSky(matrixStack);
            Vec3d vec3d = new Vec3d(0.6f,1,0.8f);
            float f = (float)vec3d.x;
            float g = (float)vec3d.y;
            float h = (float)vec3d.z;
            BackgroundRenderer.applyFogColor();
            Tessellator tessellator = Tessellator.getInstance();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(1, 1, 1, 0f);
            RenderSystem.enableBlend();
            if (true) {
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                RenderSystem.setShaderColor(1, 1, 1, 1f);
                matrixStack.push();
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0f));
                Matrix4f matrix4f2 = matrixStack.peek().getPositionMatrix();
                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f2, 0.0f, 12.0f, 0.0f).color(0, 0, 0, 1f);
                int m = 16;
                for (int n = 0; n <= 16; ++n) {
                    float o = (float)n * ((float)Math.PI * 2) / 16.0f;
                    float p = MathHelper.sin(o);
                    float q = MathHelper.cos(o);
                    bufferBuilder.vertex(matrix4f2, p * 36.0f, (float) (1+Math.sin(o+world.getTime()*0.1f)*2), -q * 36.0f).color(1f, 0, 0.6f, 0f);
                }
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                matrixStack.pop();
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
    }
    private void renderNetherSky(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        Tessellator tessellator = Tessellator.getInstance();

        RenderSystem.setShaderTexture(0, NETHER_SKY);

        for (int i = 0; i < 6; ++i) {
            matrices.push();
            if (i == 1) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            }
            if (i == 2) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
            }
            if (i == 3) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
            }
            if (i == 4) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
            }
            if (i == 5) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f));
            }
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(-14145496);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(-14145496);
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(-14145496);
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(-14145496);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }
        RenderSystem.setShaderTexture(0, NETHER_MAW);
        for(int i = 0; i < 5; i++){
            if(i > 0){
                RenderSystem.setShaderTexture(0, NETHER_MAW2);
            }
            matrices.push();
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            int mult = (i%2==0 ? -1 : 1);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((client.world.getTime()*7/(i+3f)+i*60)*mult));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -5.0f-i*2f, -6.0f+i/4f, -5.0f-i*2f).texture(0.0f, 0.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            bufferBuilder.vertex(matrix4f, -5.0f-i*2f, -6.0f+i/4f, 5.0f+i*2f).texture(0.0f, 1.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            bufferBuilder.vertex(matrix4f, 5.0f+i*2f, -6.0f+i/4f, 5.0f+i*2f).texture(1.0f, 1.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            bufferBuilder.vertex(matrix4f, 5.0f+i*2f, -6.0f+i/4f, -5.0f-i*2f).texture(1.0f, 0.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    @Inject(method = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"),cancellable = true)
    private void depthEnemies(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci){
        if(!this.entityList.contains(entity) && entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant()){
            this.entityList.add((Entity)entity);

        }
    }
    @Inject(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;drawCurrentLayer()V",ordinal = 0,shift = At.Shift.BEFORE))
    private void depthMainEnemies(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double g = vec3d.getZ();
        boolean bl3 = false;
        VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        MatrixStack matrixStack = new MatrixStack();
        float delta = tickCounter.getTickDelta(false);
        PaleUniformsEvent.getFramebuffer().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());
        RenderSystem.depthMask(true);
        PaleUniformsEvent.getFramebuffer().beginWrite(false);
        if(!this.entityList.isEmpty())
        {
            for(Entity entity : this.entityList){
                VertexConsumerProvider vertexConsumerProvider;
                if (this.canDrawEntityOutlines() && this.client.hasOutline(entity)) {
                    bl3 = true;
                    OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                    vertexConsumerProvider = outlineVertexConsumerProvider;
                    int i = entity.getTeamColorValue();
                    outlineVertexConsumerProvider.setColor(ColorHelper.Argb.getRed(i), ColorHelper.Argb.getGreen(i), ColorHelper.Argb.getBlue(i), 255);
                } else {
                    vertexConsumerProvider = PaleUniformsEvent.createPaleImmediat();
                }
                if(entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant()){
                    renderEntity(entity,d,e,g,delta,matrixStack,vertexConsumerProvider);
                }
            }
            this.entityList.clear();
        }
        PaleUniformsEvent.getFramebuffer().endWrite();
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);

    }
    @Inject(method = "renderWeather", at = @At("HEAD"))
    private void notRenderEndAtTheEmdSky(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci){
        if(MinecraftClient.getInstance().player.getPos().length()>9000&&MinecraftClient.getInstance().player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END)
        {
                manager.enable();
                MatrixStack matrices = new MatrixStack();
                Vector3f color = new Vector3f(1f,1f,1f);
                float pos = (float) (cameraY-62.5);
                float alpha = (float) Math.clamp((MinecraftClient.getInstance().player.getPos().length()-9000f)/100f,0f,1f)/2f;
                RenderSystem.depthMask(true);
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                RenderSystem.setShaderTexture(0, FOGGIEST_SKY);
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.DST_ALPHA, GlStateManager.SrcFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_DST_ALPHA);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                Tessellator tessellator = Tessellator.getInstance();
                for (int i = 0; i <1; ++i) {
                    matrices.push();
                    float timex = (float) Math.sin((double) MinecraftClient.getInstance().world.getTimeOfDay()/100+i*4)*12;
                    float timez = (float) Math.cos((double) MinecraftClient.getInstance().world.getTimeOfDay()/100+i*4)*12;
                    float mult = -1;
                    if(i>0){
                        mult = 1;
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
                    }
                    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                    BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                    double v = timez - (cameraZ % 216)*-1*mult;
                    bufferBuilder.vertex(matrix4f,(float) (-540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v-540.0f).texture(0.0f, 0.0f).color(color.x,color.y,color.z,alpha);
                    bufferBuilder.vertex(matrix4f,(float) (-540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v+540.0f).texture(0.0f, 10.0f).color(color.x,color.y,color.z,alpha);
                    bufferBuilder.vertex(matrix4f,(float) (540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v+540.0f).texture(10.0f, 10.0f).color(color.x,color.y,color.z,alpha);
                    bufferBuilder.vertex(matrix4f,(float) (540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v-540.0f).texture(10.0f, 0.0f).color(color.x,color.y,color.z,alpha);

                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                    matrices.pop();
                }

                manager.disable();
                RenderSystem.depthMask(true);
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();


        }
    }
    @Inject(method = "renderEndSky(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("TAIL"),cancellable = true)
    private void notRenderEndSky(MatrixStack matrices, CallbackInfo ci){
        if(MinecraftClient.getInstance().player.getPos().length()>9000)
        {
            Vector3f color = new Vector3f(1f,1f,1f);
            RenderSystem.enableBlend();
            float pos = (float) MinecraftClient.getInstance().cameraEntity.getPos().y;
            float alpha = (float) Math.clamp((MinecraftClient.getInstance().player.getPos().length()-9000f)/50f,0f,1f);
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.setShaderTexture(0, FOG_SKY);
            Tessellator tessellator = Tessellator.getInstance();
            for (int i = 0; i < 6; ++i) {
                matrices.push();
                float timex = (float) Math.sin((double) MinecraftClient.getInstance().world.getTimeOfDay()/16+i)*9;
                float timez = (float) Math.cos((double) MinecraftClient.getInstance().world.getTimeOfDay()/16+i)*9;
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(matrix4f, -90.0f+timex, -i, -90.0f+timez).texture(0.0f, 0.0f).color(color.x,color.y,color.z,0.6f*alpha);
                bufferBuilder.vertex(matrix4f, -90.0f+timex, -i, 90.0f+timez).texture(0.0f, 1.0f).color(color.x,color.y,color.z,0.6f*alpha);
                bufferBuilder.vertex(matrix4f, 90.0f+timex, -i, 90.0f+timez).texture(1.0f, 1.0f).color(color.x,color.y,color.z,0.6f*alpha);
                bufferBuilder.vertex(matrix4f, 90.0f+timex, -i, -90.0f+timez).texture(1.0f, 0.0f).color(color.x,color.y,color.z,0.6f*alpha);
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                matrices.pop();
            }
            for (int i = 0; i < 6; ++i) {
                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
                float timex = (float) Math.sin((double) MinecraftClient.getInstance().world.getTimeOfDay()/16+i)*9;
                float timez = (float) Math.cos((double) MinecraftClient.getInstance().world.getTimeOfDay()/16+i)*9;
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(matrix4f, -90.0f+timex, -i, -90.0f+timez).texture(0.0f, 0.0f).color(color.x,color.y,color.z,0.6f*alpha);
                bufferBuilder.vertex(matrix4f, -90.0f+timex, -i, 90.0f+timez).texture(0.0f, 1.0f).color(color.x,color.y,color.z,0.6f*alpha);
                bufferBuilder.vertex(matrix4f, 90.0f+timex, -i, 90.0f+timez).texture(1.0f, 1.0f).color(color.x,color.y,color.z,0.6f*alpha);
                bufferBuilder.vertex(matrix4f, 90.0f+timex, -i, -90.0f+timez).texture(1.0f, 0.0f).color(color.x,color.y,color.z,0.6f*alpha);
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                matrices.pop();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
//            RenderSystem.enableBlend();
//            RenderSystem.depthMask(false);
//            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
//            RenderSystem.setShaderTexture(0, FOGGIER_SKY);
//            tessellator = Tessellator.getInstance();
//            for (int i = 0; i < 6; ++i) {
//                matrices.push();
//                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().world.getTimeOfDay()/8f));
//                if (i == 1) {
//                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
//                }
//                if (i == 2) {
//                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
//                }
//                if (i == 3) {
//                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
//                }
//                if (i == 4) {
//                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
//                }
//                if (i == 5) {
//                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f));
//                }
//                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
//                bufferBuilder.vertex(matrix4f, -90.0f, -90.0f, -90.0f).texture(0.0f, 0.0f).color(color.x,color.y,color.z, alpha/2f);
//                bufferBuilder.vertex(matrix4f, -90.0f, -90.0f, 90.0f).texture(0.0f, 2.0f).color(color.x,color.y,color.z, alpha/2f);
//                bufferBuilder.vertex(matrix4f, 90.0f, -90.0f, 90.0f).texture(2.0f, 2.0f).color(color.x,color.y,color.z, alpha/2f);
//                bufferBuilder.vertex(matrix4f, 90.0f, -90.0f, -90.0f).texture(2.0f, 0.0f).color(color.x,color.y,color.z, alpha/2f);
//                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
//                matrices.pop();
//            }
//            RenderSystem.depthMask(true);
//            RenderSystem.disableBlend();
        }

    }
    @Inject(method = "render",at=@At("TAIL"))
    private void idk(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){

    }
}
