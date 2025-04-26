package net.atired.executiveorders.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
    private static final Identifier SNOW = Identifier.ofVanilla("textures/environment/snow.png");
    @Shadow private @Nullable VertexBuffer lightSkyBuffer;
    @Shadow private @Nullable ClientWorld world;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void captureFrustum();

    @Shadow protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Shadow protected abstract boolean canDrawEntityOutlines();

    @Shadow @Final private BufferBuilderStorage bufferBuilders;
    @Shadow private int ticks;
    @Shadow @Final private float[] NORMAL_LINE_DX;
    @Shadow @Final private float[] NORMAL_LINE_DZ;

    @Unique
    private static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return getLightmapCoordinates(world, world.getBlockState(pos), pos);
    }

    @Unique
    private static int getLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos pos) {
        return 15728880;
    }

    @Unique
    private final List<Integer> entityList = new ArrayList<Integer>();
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
            float offset = (float) Math.cos(client.world.getTime()/16f+i);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((client.world.getTime()*7/(i+3f)+i*60)*mult));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -5.0f-i*2f, -6.0f+i/4f+offset, -5.0f-i*2f).texture(0.0f, 0.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            bufferBuilder.vertex(matrix4f, -5.0f-i*2f, -6.0f+i/4f+offset, 5.0f+i*2f).texture(0.0f, 1.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            bufferBuilder.vertex(matrix4f, 5.0f+i*2f, -6.0f+i/4f+offset, 5.0f+i*2f).texture(1.0f, 1.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            bufferBuilder.vertex(matrix4f, 5.0f+i*2f, -6.0f+i/4f+offset, -5.0f-i*2f).texture(1.0f, 0.0f).color(ColorHelper.Argb.getArgb(255*(5-i)/6,255,255,255));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    @Inject(method = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"),cancellable = true)
    private void depthEnemies(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci){
        if(PaleUniformsEvent.createPaleImmediat()!=vertexConsumers && entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant()) {
            RenderSystem.setShaderColor(1,1,1,0.02f);
        }
    }
    @Inject(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;drawCurrentLayer()V",ordinal = 0,shift = At.Shift.AFTER))
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
        RenderSystem.enableDepthTest();
            PaleUniformsEvent.getFramebuffer().beginWrite(false);
            for(Entity entity : this.world.getEntities()){

                if(entity == null){
                    break;
                }
                VertexConsumerProvider vertexConsumerProvider= immediate;
                if (this.canDrawEntityOutlines() && this.client.hasOutline(entity)) {
                    bl3 = true;
                    OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                    vertexConsumerProvider = outlineVertexConsumerProvider;
                    int i = entity.getTeamColorValue();
                    outlineVertexConsumerProvider.setColor(ColorHelper.Argb.getRed(i), ColorHelper.Argb.getGreen(i), ColorHelper.Argb.getBlue(i), 255);
                }
                if(entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant())
                    renderEntity(entity,d,e,g,delta,matrixStack,PaleUniformsEvent.createPaleImmediat());

            }
            PaleUniformsEvent.createPaleImmediat().draw();
            PaleUniformsEvent.getFramebuffer().endWrite();
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);



    }
    @Inject(method = "renderWeather", at = @At("HEAD"))
    private void notRenderEndAtTheEmdSky(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci){
        float f = this.client.world.getRainGradient(tickDelta);
        if (!(f <= 0.0F) && MinecraftClient.getInstance().player.getPos().length()>9000&&MinecraftClient.getInstance().player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END) {
            manager.enable();
            World world = this.client.world;
            int i = MathHelper.floor(cameraX);
            int j = MathHelper.floor(cameraY);
            int k = MathHelper.floor(cameraZ);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = null;
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int l = 10;
            if (MinecraftClient.isFancyGraphicsOrBetter()) {
                l = 15;
            }

            RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
            int m = -1;
            float g = (float)this.ticks + tickDelta;
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (int n = k - l; n <= k + l; n++) {
                for (int o = i - l; o <= i + l; o++) {
                    int p = (n - k + 16) * 32 + o - i + 16;
                    double d = (double)this.NORMAL_LINE_DX[p] * 0.5;
                    double e = (double)this.NORMAL_LINE_DZ[p] * 0.5;
                    mutable.set((double)o, cameraY, (double)n);
                        float a1= (float) (Math.abs(n - k)) /l*2f;
                        float a2= (float) (Math.abs(o- i)) /l*2f;
                        int q = world.getTopY(Heightmap.Type.MOTION_BLOCKING, o, n);
                        float r2 = (float) (-l);
                        float s2 = (float) ( l);
                    float r = (float) (cameraY - l);
                    float s = (float) (cameraY + l);
                        if (r < q) {
                            r = q;
                        }

                        if (s < q) {
                            s = q;
                        }


                        int t = q;
                        if (q < j) {
                            t = j;
                        }

                        if (r != s && q < 20) {
                            Random random = Random.create((long)(o * o * 3121 + o * 45238971 ^ n * n * 418711 + n * 13761));
                            mutable.set(o, r, n);


                                if (m != 1) {
                                    if (m >= 0) {
                                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                                    }

                                    m = 1;
                                    RenderSystem.setShaderTexture(0, SNOW);
                                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                                }

                                float ad = ((float)(this.ticks & 511) + tickDelta) / 64.0f;
                                float ae = (float)(random.nextDouble() + (double)g * 0.01 * (double)((float)random.nextGaussian()));
                                float h = (float)(random.nextDouble() + (double)(g * (float)random.nextGaussian()) * 0.001);
                                double af = (double)o + 0.5 - cameraX;
                                double y = (double)n + 0.5 - cameraZ;
                                float ag = (float)Math.sqrt(af * af + y * y) / (float)l;
                                float ah = ((1.0F - ag * ag) * 0.3F + 0.5F) * f;
                                mutable.set(o, t, n);
                                int ai = getLightmapCoordinates(world, mutable);
                                int aj = ai >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295);
                                int ac = ai & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295);
                                int ak = (aj * 3 + LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE) / 4;
                                int al = (ac * 3 + LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE) / 4;
                                bufferBuilder.vertex((float)((double)o - cameraX - d + 0.5), (float)((double)s - cameraY), (float)((double)n - cameraZ - e + 0.5))
                                        .texture(0.0F + ae/4, (float) (r2 * 0.25F/4f + ad + h-cameraY/8f))
                                        .color(1.0F, 1.0F, 1.0F, 0)
                                        .light(al, ak);
                                bufferBuilder.vertex((float)((double)o - cameraX + d + 0.5), (float)((double)s - cameraY), (float)((double)n - cameraZ + e + 0.5))
                                        .texture((1.0F + ae)/4, (float) (r2 * 0.25F/4f + ad + h-cameraY/8f))
                                        .color(1.0F, 1.0F, 1.0F, 0)
                                        .light(al, ak);
                            float clamp = Math.clamp(ah * (a1 + a2), 0, 1);
                            bufferBuilder.vertex((float)((double)o - cameraX + d + 0.5), (float)((double)r - cameraY), (float)((double)n - cameraZ + e + 0.5))
                                        .texture((float) ((1.0F + ae)/4+Math.sin(((float)(this.ticks & 511) + tickDelta)/16)), (float) (s2 * 0.25F/4f + ad + h-cameraY/8f))
                                        .color(1.0F, 1.0F, 1.0F, clamp)
                                        .light(al, ak);
                                bufferBuilder.vertex((float)((double)o - cameraX - d + 0.5), (float)((double)r - cameraY), (float)((double)n - cameraZ - e + 0.5))
                                        .texture((float) ((0.0F + ae)/4+Math.sin(((float)(this.ticks & 511) + tickDelta)/16)), (float) (s2 * 0.25F/4f + ad + h-cameraY/8f))
                                        .color(1.0F, 1.0F, 1.0F, clamp)
                                        .light(al, ak);

                        }

                }
            }

            if (m >= 0) {
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            manager.disable();
        }

//        if(MinecraftClient.getInstance().player.getPos().length()>9000&&MinecraftClient.getInstance().player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END)
//        {
//                manager.enable();
//                MatrixStack matrices = new MatrixStack();
//                Vector3f color = new Vector3f(1f,1f,1f);
//                float pos = (float) (cameraY-62.5);
//                float alpha = (float) Math.clamp((MinecraftClient.getInstance().player.getPos().length()-9000f)/100f,0f,1f)/2f;
//                RenderSystem.depthMask(true);
//                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
//                RenderSystem.setShaderTexture(0, FOGGIEST_SKY);
//                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.DST_ALPHA, GlStateManager.SrcFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_DST_ALPHA);
//                RenderSystem.enableBlend();
//                RenderSystem.enableDepthTest();
//                Tessellator tessellator = Tessellator.getInstance();
//                for (int i = 0; i <1; ++i) {
//                    matrices.push();
//                    float timex = (float) Math.sin((double) MinecraftClient.getInstance().world.getTimeOfDay()/100+i*4)*12;
//                    float timez = (float) Math.cos((double) MinecraftClient.getInstance().world.getTimeOfDay()/100+i*4)*12;
//                    float mult = -1;
//                    if(i>0){
//                        mult = 1;
//                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
//                    }
//                    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//                    BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
//                    double v = timez - (cameraZ % 216)*-1*mult;
//                    bufferBuilder.vertex(matrix4f,(float) (-540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v-540.0f).texture(0.0f, 0.0f).color(color.x,color.y,color.z,alpha);
//                    bufferBuilder.vertex(matrix4f,(float) (-540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v+540.0f).texture(0.0f, 10.0f).color(color.x,color.y,color.z,alpha);
//                    bufferBuilder.vertex(matrix4f,(float) (540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v+540.0f).texture(10.0f, 10.0f).color(color.x,color.y,color.z,alpha);
//                    bufferBuilder.vertex(matrix4f,(float) (540.0f+timex-cameraX%216), -4+mult*pos-i, (float) v-540.0f).texture(10.0f, 0.0f).color(color.x,color.y,color.z,alpha);
//
//                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
//                    matrices.pop();
//                }
//
//                manager.disable();
//                RenderSystem.depthMask(true);
//                RenderSystem.defaultBlendFunc();
//                RenderSystem.disableBlend();
//
//
//        }
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
            for (int i = 1; i < 6; ++i) {
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
            for (int i = 1; i < 6; ++i) {
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
