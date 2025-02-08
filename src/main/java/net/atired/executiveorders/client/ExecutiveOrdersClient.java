package net.atired.executiveorders.client;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.accessors.PersistentProjectileEntityAccessor;
import net.atired.executiveorders.client.event.*;
import net.atired.executiveorders.client.renderers.blockentity.MonolithBlockEntityRenderer;
import net.atired.executiveorders.client.renderers.entity.JauntRenderer;
import net.atired.executiveorders.client.renderers.entity.VitrifiedRenderer;
import net.atired.executiveorders.enemies.blockentity.MonolithBlockEntity;
import net.atired.executiveorders.init.*;
import net.atired.executiveorders.networking.payloads.*;
import net.atired.executiveorders.particles.custom.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
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

import java.awt.*;

public class ExecutiveOrdersClient implements ClientModInitializer {
    private static final ManagedShaderEffect PALE_PROGRAM;
    private static final ManagedShaderEffect ROOF_PROGRAM;
    private static final ManagedShaderEffect AFTERBURN_PROGRAM;
    private static final Uniform1f paleFadeIn;
    public static final Uniform2f paleCamRot;
    public static final SamplerUniformV2 paleSampler;
    public static final SamplerUniformV2 paleSampler2;
    private static final Uniform1f paleTime;
    private static final Uniform1f burnTime;
    private static final Uniform1f burnFadeIn;
    @Override
    public void onInitializeClient() {

        CoreShaderRegistrationCallback.EVENT.register(new CoreShaderRegistrationEvent());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksInit.MONOLITH, RenderLayer.getCutout());
        BlockEntityRendererRegistry.register(BlockEntityInit.MONOLITH_ENTITY_TYPE, MonolithBlockEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EntityTypeInit.VITRIFIED, VitrifiedRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EntityTypeInit.JAUNT, JauntRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ParticlesInit.EXECUTE_PARTICLE, ExecuteParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticlesInit.EFFIGY_PARTICLE, EffigyParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticlesInit.SPLISH_PARTICLE, SplishParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticlesInit.HAUNTED_SLASH_PARTICLE, HauntedSlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticlesInit.HAUNTED_BOUNCE_PARTICLE, HauntedBounceParticle.Factory::new);
        initEvents();
        initColours();
        initpayloads();
    }
    private void initColours(){
        ColorProviderRegistry.ITEM.register((provider, objects)->{
            if(provider.get(EODataComponentTypeInit.AXEHEAT)!=null&&objects==1){
                float axeheat = provider.get(EODataComponentTypeInit.AXEHEAT)/200f;
                if(axeheat>0.1){
                    return ColorHelper.Argb.fromFloats(Math.clamp(axeheat*3+0.2f,0,1),0.5f,0f,1f);
                }
                return ColorHelper.Argb.fromFloats(axeheat,0.5f,0f,1f);
            }
            return Color.HSBtoRGB(1,0,1);
        }, ItemsInit.HAUNTED_AXE);
    }
    private void initpayloads(){
        PayloadTypeRegistry.playS2C().register(ExecutePayload.ID, ExecutePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ExecutePayload.ID, (payload, context) -> {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if(entity instanceof LivingEntityAccessor accessor)
            {
                accessor.setExecuteTime(20);
            }
        });
        PayloadTypeRegistry.playS2C().register(DepthsPayload.ID, DepthsPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(DepthsPayload.ID, (payload, context) -> {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if(entity instanceof DepthsLivingEntityAccessor accessor)
            {
                accessor.executiveOrders$setRadiant(true);
            }
        });
        PayloadTypeRegistry.playS2C().register(MonolithPayload.ID, MonolithPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(MonolithPayload.ID, (payload, context) -> {
            MonolithBlockEntity monolithBlock =  ((MonolithBlockEntity)context.client().world.getBlockEntity(new BlockPos(payload.x(),payload.y(), payload.z())));
            if(monolithBlock!=null)
                monolithBlock.alphaticks = payload.value();
        });
        PayloadTypeRegistry.playS2C().register(ArbalestPayload.ID, ArbalestPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ArbalestPayload.ID, (payload, context) -> {
            Entity entity = context.client().world.getEntityById(payload.entityID());
            if(entity instanceof PersistentProjectileEntityAccessor accessor)
            {
                accessor.setProjScale(payload.scale());
            }
        });
        PayloadTypeRegistry.playS2C().register(HauntedAxePayload.ID, HauntedAxePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(HauntedAxePayload.ID, new HauntedAxePayload.Receiver());
        PayloadTypeRegistry.playS2C().register(PreciseImpactPayload.ID, PreciseImpactPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(PreciseImpactPayload.ID, new PreciseImpactPayload.Receiver());

    }
    private void initEvents() {
        HudRenderCallback.EVENT.register(new HollowCoreRenderEvent());
        HudRenderCallback.EVENT.register(new ThunderedRenderEvent());
        HudRenderCallback.EVENT.register(new MarkiplierEvent());

        PostWorldRenderCallback.EVENT.register((camera, tickDelta)->{
        });
        EntitiesPreRenderCallback.EVENT.register((camera, frustum, tickDelta) -> {

            PaleUniformsEvent.getFramebuffer().clear(true);
            PaleUniformsEvent.getFramebuffer().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());
            if(MinecraftClient.getInstance().world != null)
                uniformSTime.set((MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.05f);
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
    public static void renderBurnProgram(float ticks){
        AFTERBURN_PROGRAM.render(ticks);
    }
    public static void renderRoofProgram(float ticks){
        ROOF_PROGRAM.render(ticks);
    }
    public static final ManagedCoreShader VITRIC = ShaderEffectManager.getInstance().manageCoreShader(ExecutiveOrders.id("vitric"));
    private static final Uniform1f uniformSTime = VITRIC.findUniform1f("STime");

    static {
        PALE_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/pale.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        ROOF_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/roof.json"), shader ->{
            shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).getStillDepthMap());
        });
        AFTERBURN_PROGRAM = ShaderEffectManager.getInstance().manage(ExecutiveOrders.id("shaders/post/afterburn.json"));
        paleSampler = PALE_PROGRAM.findSampler("SculkSampler");
        paleSampler2 = PALE_PROGRAM.findSampler("ActualSculkSampler");
        paleFadeIn = PALE_PROGRAM.findUniform1f("Fade");
        paleTime = PALE_PROGRAM.findUniform1f("GameTime");
        paleCamRot = PALE_PROGRAM.findUniform2f("CamRot");
        burnTime = AFTERBURN_PROGRAM.findUniform1f("GameTime");
        burnFadeIn = AFTERBURN_PROGRAM.findUniform1f("Burn");
        paleFadeIn.set(0);
    }
}
