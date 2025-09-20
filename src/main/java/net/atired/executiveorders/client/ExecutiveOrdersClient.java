package net.atired.executiveorders.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.ClientWorldAccessor;
import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.accessors.PersistentProjectileEntityAccessor;
import net.atired.executiveorders.client.event.*;
import net.atired.executiveorders.client.renderers.IcoSphere;
import net.atired.executiveorders.client.renderers.blockentity.BrushableBedrockEntityRenderer;
import net.atired.executiveorders.client.renderers.blockentity.MonolithBlockEntityRenderer;
import net.atired.executiveorders.client.renderers.blockentity.VitricCampfireBlockEntityRenderer;
import net.atired.executiveorders.client.renderers.entity.*;
import net.atired.executiveorders.enemies.blockentity.MonolithBlockEntity;
import net.atired.executiveorders.init.*;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.atired.executiveorders.networking.payloads.*;
import net.atired.executiveorders.particles.custom.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.dimension.DimensionTypes;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.ladysnake.satin.api.event.EntitiesPostRenderCallback;
import org.ladysnake.satin.api.event.EntitiesPreRenderCallback;
import org.ladysnake.satin.api.event.PostWorldRenderCallback;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.SamplerUniformV2;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.ladysnake.satin.api.managed.uniform.Uniform2f;
import org.ladysnake.satin.api.managed.uniform.Uniform3f;

import java.awt.*;
import java.util.List;

public class ExecutiveOrdersClient implements ClientModInitializer {
    private static final ManagedShaderEffect PALE_PROGRAM;
    private static final ManagedShaderEffect PALEBORDER_PROGRAM;
    private static final ManagedShaderEffect VOIDPAR_PROGRAM;
    private static final ManagedShaderEffect NOSKY_PROGRAM;
    private static final ManagedShaderEffect ROOF_PROGRAM;
    private static final ManagedShaderEffect AFTERBURN_PROGRAM;
    public static ParticleTextureSheet PARTICLE_SHEET_VOID = new ParticleTextureSheet() {
        @Override
        public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        public String toString() {
            return "PARTICLE_SHEET_VOID";
        }
    };
    public static ParticleTextureSheet PARTICLE_SHEET_SKY = new ParticleTextureSheet() {
        @Override
        public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        public String toString() {
            return "PARTICLE_SHEET_SKY";
        }
    };
    private static final Uniform1f paleFadeIn;
    public static final Uniform2f paleCamRot;
    public static final Uniform1f palebordFadeIn;
    public static final Uniform2f palebordCamRot;
    public static final Uniform1f voidTime;
    public static final SamplerUniformV2 paleSampler;
    public static final SamplerUniformV2 paleSamplerDepth;
    public static final SamplerUniformV2 voidSampler;
    public static final SamplerUniformV2 voidSampler2;
    public static final SamplerUniformV2 skySampler;
    public static final SamplerUniformV2 skySampler2;
    public static final SamplerUniformV2 skySampler3;
    public static final SamplerUniformV2 paleSampler2;

    private static final Uniform1f paleTime;
    public static final Uniform1f palebordTime;
    public static final Uniform3f skyPos;
    public static final Uniform2f skyRot;
    public static final Uniform1f skyTime;
    public static final Uniform1f skyPower;
    public static final Uniform1f roofTime;
    public static final Uniform1f roofDark;
    private static final Uniform1f burnTime;
    private static final Uniform1f burnFadeIn;
    public static IcoSphere SPHERE= IcoSphere.MakeIcosphere(2);

    @Override
    public void onInitializeClient() {

        CoreShaderRegistrationCallback.EVENT.register(new CoreShaderRegistrationEvent());
        BlockRenderLayerMap.INSTANCE.putBlock(EOBlocksInit.MONOLITH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EOBlocksInit.BEDROCK_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EOBlocksInit.VITRIC_CAMPFIRE, RenderLayer.getCutout());
        BlockEntityRendererRegistry.register(EOBlockEntityInit.MONOLITH_ENTITY_TYPE, MonolithBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(EOBlockEntityInit.VITRIC_CAMPFIRE_ENTITY_TYPE, VitricCampfireBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(EOBlockEntityInit.BRUSHABLE_BLOCK_BEDROCK, BrushableBedrockEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EOEntityTypeInit.VITRIFIED, VitrifiedRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EOEntityTypeInit.JAUNT, JauntRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EOEntityTypeInit.ICOSPHERE, IcoSphereEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EOEntityTypeInit.STARFALL, StarFallRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EOEntityTypeInit.DEATHRAY, DeathRayRenderer::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.SMALL_SKY_PARTICLE, SmallSkyParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.SKY_PARTICLE, SkyParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.SMALL_VOID_PARTICLE, SmallVoidParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.VOID_PARTICLE, VoidParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.EXECUTE_PARTICLE, ExecuteParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.EFFIGY_PARTICLE, EffigyParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.SPLISH_PARTICLE, SplishParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.SANDED_PARTICLE, SandedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.HAUNTED_SLASH_PARTICLE, HauntedSlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.HAUNTED_BOUNCE_PARTICLE, HauntedBounceParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EOParticlesInit.BEDROCKLEAF_PARTICLE, BedrockLeafParticle.Factory::new);
        initEvents();
        initColours();
        initpayloads();

    }
    private void initColours(){
        ModelPredicateProviderRegistry.register(EOItemsInit.WARHORN,Identifier.ofVanilla("tooting"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex)->{
            if(pos.getY()>124){
                float otherNoise = MathHelper.clamp((EOgetDatNoise.sampleNoise3D(pos.getX(),pos.getY(),pos.getZ(),5)+3.14f+0.8f)/2/3.14f,0f,1f);
                otherNoise = 1-Math.max(otherNoise-0.6f,0f)*2.5f;

                float yTrue = MathHelper.clamp((pos.getY()-124+(EOgetDatNoise.sampleNoise3D(pos.getX(),0,pos.getZ(),30))*8f)/40f,0f,1f);
                return Color.HSBtoRGB(1-0.2f*yTrue,yTrue/1.5f*otherNoise,MathHelper.clamp(0.1f+yTrue*0.9f/(otherNoise+0.1f),0,1f));
            }
            return Color.HSBtoRGB(1,0,0.1f);
        }, EOBlocksInit.BEDROCK_LEAVES);
        ColorProviderRegistry.ITEM.register((provider, objects)->{
            if(provider.get(EODataComponentTypeInit.AXEHEAT)!=null&&objects==1){
                float axeheat = provider.get(EODataComponentTypeInit.AXEHEAT)/200f;
                if(axeheat>0.1){
                    return ColorHelper.Argb.fromFloats(Math.clamp(axeheat*3+0.2f,0,1),0.5f,0f,1f);
                }
                return ColorHelper.Argb.fromFloats(axeheat,0.5f,0f,1f);
            }
            return Color.HSBtoRGB(1,0,1);
        }, EOItemsInit.HAUNTED_AXE);
        ColorProviderRegistry.ITEM.register((provider, objects)-> Color.HSBtoRGB(1,0.1f,0.3f), EOBlocksInit.BEDROCK_LEAVES.asItem());

    }
    private void initpayloads(){

        ClientPlayNetworking.registerGlobalReceiver(ExecutePayload.ID, (payload, context) -> {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if(entity instanceof LivingEntityAccessor accessor)
            {
                accessor.setExecuteTime(20);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(DepthsPayload.ID, (payload, context) -> {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if(entity instanceof DepthsLivingEntityAccessor accessor)
            {
                accessor.executiveOrders$setRadiant(true);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(MonolithPayload.ID, (payload, context) -> {
            MonolithBlockEntity monolithBlock =  ((MonolithBlockEntity)context.client().world.getBlockEntity(new BlockPos(payload.x(),payload.y(), payload.z())));
            if(monolithBlock!=null)
                monolithBlock.alphaticks = payload.value();
        });

        ClientPlayNetworking.registerGlobalReceiver(ArbalestPayload.ID, (payload, context) -> {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if(entity instanceof PersistentProjectileEntityAccessor accessor)
            {
                accessor.setProjScale(payload.scale());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(WarHornPayload.ID, (payload, context) -> {
            if(context.player().getWorld() instanceof ClientWorldAccessor accessor){
                accessor.executiveOrders$setRipple(payload.scale());
                context.player().setVelocity(new Vec3d(payload.vector3f()));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(HauntedAxePayload.ID, new HauntedAxePayload.Receiver());
        
        ClientPlayNetworking.registerGlobalReceiver(PreciseImpactPayload.ID, new PreciseImpactPayload.Receiver());

    }
    private static final Identifier SKY_NO = ExecutiveOrders.id("textures/misc/nosky.png");
    private void initEvents() {
        ClientReceiveMessageEvents.ALLOW_CHAT.register((text,message,profile,parameters,timed)->{
            if(MinecraftClient.getInstance().player.getInventory().contains((itemStack)->{
                if(itemStack.getItem()== EOItemsInit.NIGHTMARE_FUEL){
                    return true;
                }
                return false;
            }))
            {
                MutableText text1 = Text.of("").copy();
                List<Text> textList = text.getWithStyle(text.getStyle());
                for(int i = 0; i < textList.size()-1;i++){
                    text1 = text1.append(textList.get(i));
                }
                text1 = text1.append(text.getWithStyle(text.getStyle().withFont(ExecutiveOrders.id("wrong"))).getLast());
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text1);
                return false;
            }
            return true;
        });
        HudRenderCallback.EVENT.register(new HollowCoreRenderEvent());
        TooltipComponentCallback.EVENT.register(new PalePileTooltipComponentEvent());
        HudRenderCallback.EVENT.register(new ThunderedRenderEvent());
        HudRenderCallback.EVENT.register(new MarkiplierEvent());
        PostWorldRenderCallback.EVENT.register((camera, tickDelta)->{
            PaleUniformsEvent.getFramebufferSky2().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());

            PaleUniformsEvent.getFramebufferPar2().beginWrite(false);
            RenderSystem.depthMask(true);

            PaleUniformsEvent.createDarkImmediate().draw();

            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
        });

        WorldRenderEvents.BEFORE_ENTITIES.register((context -> {

                }));
        WorldRenderEvents.AFTER_ENTITIES.register((context -> {
            if(context.camera()!=null&&MinecraftClient.getInstance().player!=null&&MinecraftClient.getInstance().player.getPos().length()>2000&&MinecraftClient.getInstance().player.getPos().length()<3000 && (MinecraftClient.getInstance().player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END)){
                float time = (context.tickCounter().getTickDelta(false)+MinecraftClient.getInstance().world.getTime())/40;
                float alpha = Math.min((float) Math.clamp((MinecraftClient.getInstance().player.getPos().length()-2000)/50,0,1), (float) Math.clamp((3000-MinecraftClient.getInstance().player.getPos().length())/50,0,1));
                MatrixStack matrixStack = new MatrixStack();
                PaleUniformsEvent.getFramebufferSky2().beginWrite(false);
                for (int j = 0; j < 4; j++) {
                    matrixStack.push();



                    Vec3d vec3d = context.camera().getPos();

                    RenderSystem.depthMask(true);
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();

                    Matrix4f matrix4f1 = matrixStack.peek().getPositionMatrix();
                    if(matrix4f1==null){
                        return;
                    }
                    Vec3d[] array = new Vec3d[]{
                            new Vec3d(-1.5F, -4.0F, -1.5F),
                            new Vec3d(-1.5F, -4.0F, 1.5F),
                            new Vec3d(1.5F, -4.0F, 1.5F),
                            new Vec3d(1.5F, -4.0F, -1.5F)};
                    int i = 0;
                    PlayerEntity playerEntity = (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
                    float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
                    float g = -(playerEntity.horizontalSpeed + f * context.tickCounter().getTickDelta(false));
                    float h = MathHelper.lerp(context.tickCounter().getTickDelta(false), playerEntity.prevStrideDistance, playerEntity.strideDistance);
                    float anglehor = (float) (time/160+j/4f*Math.PI);
                    if(j>3){
                        anglehor+= (float) ((-time/159+0.25f)*Math.PI);
                    }
                    matrix4f1.rotate(RotationAxis.POSITIVE_X.rotationDegrees(-Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F));

                    matrix4f1.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(-MathHelper.sin(g * (float) Math.PI) * h * 3.0F));

                    matrix4f1.translate((float) (-MathHelper.sin(g * (float) Math.PI) * h * 0.5F*Math.cos(anglehor*Math.PI)*0.6f), Math.abs(MathHelper.cos(g * (float) Math.PI) * h), (float) (MathHelper.sin(g * (float) Math.PI) * h * 0.5F*Math.sin(anglehor*Math.PI)*0.6f));

                    for(Vec3d vec3d1 : array){
                        if(j>3)
                        {
                            vec3d1 = vec3d1.multiply(0.5,2,0.5);
                            vec3d1 = new Vec3d(vec3d1.toVector3f().rotate(
                                    new Quaternionf().rotationZYX(
                                            0
                                            ,(float)((anglehor)*Math.PI)
                                            ,(float)((0.6f+Math.sin(time/32+j*3)/12+Math.cos(time/100+j*3)/8)*Math.PI))));
                        }
                        else{
                            vec3d1 = new Vec3d(vec3d1.toVector3f().rotate(
                                    new Quaternionf().rotationZYX(
                                            0
                                            ,(float)((anglehor)*Math.PI)
                                            ,(float)((0.6f+Math.sin(time/12+j*3)/16+Math.cos(time/50+j*3)/8)*Math.PI))));
                        }



                        vec3d1 = vec3d1.multiply(10);
                        array[i] = vec3d1;
                        i+=1;
                    }

                    VertexConsumer consumer = PaleUniformsEvent.createSkyImmediate().getBuffer(ExecutiveOrdersClient.NOSKY.getRenderLayer(RenderLayer.getEntityTranslucent(SKY_NO)));
                    uniformSOff.set(0);
                    consumer.vertex(matrix4f1,(float)array[0].x,(float)array[0].y,(float)array[0].z).color(1f,1f,1f,alpha).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(-1, 0, 0);
                    consumer.vertex(matrix4f1,(float)array[1].x,(float)array[1].y,(float)array[1].z).color(1f,1f,1f,alpha).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(-1, 0, 0);
                    consumer.vertex(matrix4f1,(float)array[2].x,(float)array[2].y,(float)array[2].z).color(1f,1f,1f,alpha).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(-1, 0, 0);
                    consumer.vertex(matrix4f1,(float)array[3].x,(float)array[3].y,(float)array[3].z).color(1f,1f,1f,alpha).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(255).normal(-1, 0, 0);
                    matrixStack.pop();


                    matrix4f1.translate((float) (MathHelper.sin(g * (float) Math.PI) * h * 0.5F*Math.cos(anglehor*Math.PI)), -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), (float) (MathHelper.sin(g * (float) Math.PI) * h * 0.5F*Math.sin(anglehor*Math.PI)));
                    matrix4f1.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0F));
                    matrix4f1.rotate(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F));

                }
                PaleUniformsEvent.createSkyImmediate().draw();
                PaleUniformsEvent.getFramebufferSky2().endWrite();
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }




        }));
        EntitiesPostRenderCallback.EVENT.register((camera, frustum, tickDelta)->{

            PaleUniformsEvent.getFramebuffer().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());
            PaleUniformsEvent.getFramebufferPar2().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());
            PaleUniformsEvent.getFramebufferPar().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());
            PaleUniformsEvent.getFramebufferPar3().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());

            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        });
        EntitiesPreRenderCallback.EVENT.register((camera, frustum, tickDelta) -> {

            PaleUniformsEvent.getFramebuffer().clear(true);

            PaleUniformsEvent.getFramebufferPar().clear(true);
            PaleUniformsEvent.getFramebufferPar3().clear(true);
            PaleUniformsEvent.getFramebufferPar2().clear(true);
            if(MinecraftClient.getInstance().world != null){
                uniformSTime.set((MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.05f);
                uniform2Time.set((MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.05f);
                uniform3Time.set((MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.05f);

            }

            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        });
        ShaderEffectRenderCallback.EVENT.register(new PaleUniformsEvent());
    }
    public static void setPaleFadeIn(float value){
        paleFadeIn.set(value);
    }
    public static void setBurnFadeIn(float value){
        burnFadeIn.set(value);
    }
    public static void setPaleTime(float value){
        paleTime.set(value);
    }
    public static void setBurnTime(float value){
        burnTime.set(value);
    }
    public static void renderPaleProgram(float ticks){
        PALE_PROGRAM.render(ticks);
    }
    public static void renderPaleBorderProgram(float ticks){
        PALEBORDER_PROGRAM.render(ticks);
    }
    public static void renderBurnProgram(float ticks){
        AFTERBURN_PROGRAM.render(ticks);
    }
    public static void renderRoofProgram(float ticks){
        ROOF_PROGRAM.render(ticks);
    }
    public static void renderVoidParProgram(float ticks){
        VOIDPAR_PROGRAM.render(ticks);
    }
    public static void renderNoSkyProgram(float ticks){
        NOSKY_PROGRAM.render(ticks);
    }
    public static final ManagedCoreShader VITRIC = ShaderEffectManager.getInstance().manageCoreShader(ExecutiveOrders.id("vitric"));

    private static final Uniform1f uniformSTime = VITRIC.findUniform1f("STime");
    public static final ManagedCoreShader NOSKY = ShaderEffectManager.getInstance().manageCoreShader(ExecutiveOrders.id("nosky"));

    public static final ManagedCoreShader ICOSPHERE_BG = ShaderEffectManager.getInstance().manageCoreShader(Identifier.ofVanilla("executive_icosphere_bg"));
    private static final Uniform1f uniform3Time = ICOSPHERE_BG.findUniform1f("STime");
    private static final Uniform1f uniform2Time = NOSKY.findUniform1f("STime");
    public static final Uniform1f uniformSOff = NOSKY.findUniform1f("Offset");

    static {
        NOSKY_PROGRAM= ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/nosky.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        VOIDPAR_PROGRAM= ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/voidpar.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        PALE_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/pale.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        PALEBORDER_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/paleborder.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        ROOF_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/roof.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        AFTERBURN_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/afterburn.json"));
        roofTime = ROOF_PROGRAM.findUniform1f("GameTime");
        roofDark = ROOF_PROGRAM.findUniform1f("RealDarkness");
        skyTime = NOSKY_PROGRAM.findUniform1f("GameTime");
        paleSampler = PALE_PROGRAM.findSampler("SculkSampler");
        voidSampler = VOIDPAR_PROGRAM.findSampler("ParSampler");
        voidSampler2 = VOIDPAR_PROGRAM.findSampler("ParSampler2");
        skySampler = NOSKY_PROGRAM.findSampler("SkySampler");
        skySampler2 = NOSKY_PROGRAM.findSampler("OutlineSampler");
        skySampler3 = NOSKY_PROGRAM.findSampler("OutlineParSampler");
        skyPos = NOSKY_PROGRAM.findUniform3f("PosOfYou");
        skyRot = NOSKY_PROGRAM.findUniform2f("RotOfYou");
        paleSampler2 = PALE_PROGRAM.findSampler("ActualSculkSampler");
        skyPower = NOSKY_PROGRAM.findUniform1f("Strength");
        paleSamplerDepth = PALE_PROGRAM.findSampler("SculkSamplerDepth");
        paleFadeIn = PALE_PROGRAM.findUniform1f("Fade");
        palebordFadeIn = PALEBORDER_PROGRAM.findUniform1f("Fade");
        paleTime = PALE_PROGRAM.findUniform1f("GameTime");
        paleCamRot = PALE_PROGRAM.findUniform2f("CamRot");
        palebordCamRot = PALEBORDER_PROGRAM.findUniform2f("CamRot");

        palebordTime = PALEBORDER_PROGRAM.findUniform1f("GameTime");
        burnTime = AFTERBURN_PROGRAM.findUniform1f("GameTime");
        voidTime = AFTERBURN_PROGRAM.findUniform1f("GameTime");
        burnFadeIn = AFTERBURN_PROGRAM.findUniform1f("Burn");
        paleFadeIn.set(0);
        palebordCamRot.set(1,1);
    }
}
