package net.atired.executiveorders.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.ClientWorldAccessor;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.client.layers.ExecutiveRenderLayers;
import net.atired.executiveorders.init.EOMobEffectsInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionTypes;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;

import java.util.SequencedMap;

public class PaleUniformsEvent implements ShaderEffectRenderCallback {
    private static Framebuffer framebuffer;
    private static final Identifier MONOLITH = ExecutiveOrders.id("textures/block/vitric_fire2.png");
    private static final Identifier FOG_SKY = ExecutiveOrders.id("textures/misc/ruhroh.png");
    public static Framebuffer getFramebuffer() {
        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebuffer.setClearColor(0f, 0f, 0f, 1.0f);
        }
        else if(framebuffer.viewportHeight != MinecraftClient.getInstance().getFramebuffer().viewportHeight||framebuffer.viewportWidth!=MinecraftClient.getInstance().getFramebuffer().viewportWidth){
            framebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebuffer.setClearColor(1f, 1f, 1f, 0.0f);
        }
        return framebuffer;
    }
    private static Framebuffer framebufferPar;
    public static Framebuffer getFramebufferPar() {
        if (framebufferPar == null) {
            framebufferPar = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferPar.setClearColor(1f, 1f, 1f, 0.0f);
        }
        else if(framebufferPar.viewportHeight != MinecraftClient.getInstance().getFramebuffer().viewportHeight||framebufferPar.viewportWidth!=MinecraftClient.getInstance().getFramebuffer().viewportWidth){
            framebufferPar = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferPar.setClearColor(1f, 1f, 1f, 0.0f);
        }
        return framebufferPar;
    }
    private static Framebuffer framebufferPar2;
    public static Framebuffer getFramebufferPar2() {
        if (framebufferPar2 == null) {
            framebufferPar2 = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferPar2.setClearColor(1f, 1f, 1f, 0.0f);
        }
        else if(framebufferPar2.viewportHeight != MinecraftClient.getInstance().getFramebuffer().viewportHeight||framebufferPar2.viewportWidth!=MinecraftClient.getInstance().getFramebuffer().viewportWidth){
            framebufferPar2 = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferPar2.setClearColor(1f, 1f, 1f, 0.0f);
        }
        return framebufferPar2;
    }
    private static Framebuffer framebufferPar3;
    public static Framebuffer getFramebufferPar3() {
        if (framebufferPar3 == null) {
            framebufferPar3 = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferPar3.setClearColor(1f, 1f, 1f, 0.0f);
        }
        else if(framebufferPar3.viewportHeight != MinecraftClient.getInstance().getFramebuffer().viewportHeight||framebufferPar3.viewportWidth!=MinecraftClient.getInstance().getFramebuffer().viewportWidth){
            framebufferPar3 = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferPar3.setClearColor(1f, 1f, 1f, 0.0f);
        }
        return framebufferPar3;
    }
    static VertexConsumerProvider.Immediate paleImmediate = null;
    public static VertexConsumerProvider.Immediate createPaleImmediat() {
        if (paleImmediate == null) {
            SequencedMap<RenderLayer, BufferAllocator> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(ExecutiveRenderLayers.getExecutiveJelly(MONOLITH), new BufferAllocator(ExecutiveRenderLayers.getExecutiveJelly(MONOLITH).getExpectedBufferSize()));
            paleImmediate = VertexConsumerProvider.immediate(buffers,new BufferAllocator(256));
        }
        return paleImmediate;
    }
    private static Framebuffer framebufferSky;
    public static Framebuffer getFramebufferSky() {
        if (framebufferSky == null) {
            framebufferSky = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferSky.setClearColor(1f, 1f, 1f, 0.0f);
        }
        else if(framebufferSky.viewportHeight != MinecraftClient.getInstance().getFramebuffer().viewportHeight||framebufferSky.viewportWidth!=MinecraftClient.getInstance().getFramebuffer().viewportWidth){
            framebufferSky = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferSky.setClearColor(1f, 1f, 1f, 0.0f);
        }
        return framebufferSky;
    }
    private static Framebuffer framebufferSky2;
    public static Framebuffer getFramebufferSky2() {
        if (framebufferSky2 == null) {
            framebufferSky2 = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferSky2.setClearColor(1f, 1f, 1f, 0.0f);
        }
        else if(framebufferSky2.viewportHeight != MinecraftClient.getInstance().getFramebuffer().viewportHeight||framebufferSky2.viewportWidth!=MinecraftClient.getInstance().getFramebuffer().viewportWidth){
            framebufferSky2 = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebufferSky2.setClearColor(1f, 1f, 1f, 0.0f);
        }
        return framebufferSky2;
    }
    static VertexConsumerProvider.Immediate darkImmediate = null;
    private float scrongled = 0.0f;
    private float bongled = 0.0f;
    public static VertexConsumerProvider.Immediate createDarkImmediate() {
        if (darkImmediate == null) {
            SequencedMap<RenderLayer, BufferAllocator> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(RenderLayer.getEntityTranslucent(MONOLITH), new BufferAllocator(RenderLayer.getEntityTranslucent(FOG_SKY).getExpectedBufferSize()));
            buffers.put(RenderLayer.getEntityTranslucent(MONOLITH), new BufferAllocator(RenderLayer.getEntityTranslucent(MONOLITH).getExpectedBufferSize()));
            darkImmediate = VertexConsumerProvider.immediate(buffers,new BufferAllocator(256));
        }
        return darkImmediate;
    }
    private static final Identifier SKY_NO = ExecutiveOrders.id("textures/misc/nosky.png");
    public static VertexConsumerProvider.Immediate createSkyImmediate() {
        if (darkImmediate == null) {
            SequencedMap<RenderLayer, BufferAllocator> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(RenderLayer.getEntityTranslucent(SKY_NO), new BufferAllocator(RenderLayer.getEntityTranslucent(SKY_NO).getExpectedBufferSize()));
            darkImmediate = VertexConsumerProvider.immediate(buffers,new BufferAllocator(256));
        }
        return darkImmediate;
    }
    @Override
    public void renderShaderEffects(float v) {
        int fps = MinecraftClient.getInstance().getCurrentFps();

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if(client.player.getPos().length()>2000&&client.player.getPos().length()<3000 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END)){
                float offscale = 1f;
                if(MinecraftClient.getInstance().world instanceof ClientWorldAccessor accessor){
                    offscale = 1f-accessor.executiveOrders$getIcoPower();

                }
                ExecutiveOrdersClient.skyPower.set( Math.min((float) Math.clamp((MinecraftClient.getInstance().player.getPos().length()-2000)/40,0,1), (float) Math.clamp((3000-MinecraftClient.getInstance().player.getPos().length())/40,0,1)));
                ExecutiveOrdersClient.skyTime.set(((MinecraftClient.getInstance().world.getTime()+v)/24000f));
                ExecutiveOrdersClient.skyPos.set(client.cameraEntity.getLerpedPos(v).toVector3f().add(0,-MinecraftClient.getInstance().world.getDimension().minY(),0));
                ExecutiveOrdersClient.skyRot.set(client.cameraEntity.getYaw(),offscale);
                getFramebufferSky().beginRead();

                if(offscale<0.2f){
                    ExecutiveOrdersClient.skySampler.set(MinecraftClient.getInstance().getTextureManager().getTexture(ExecutiveOrders.id("textures/misc/bsod.png")));
                }
                else{
                    ExecutiveOrdersClient.skySampler.set(getFramebufferSky().getColorAttachment());
                }
                getFramebufferSky2().beginRead();
                ExecutiveOrdersClient.skySampler2.set(getFramebufferSky2()::getColorAttachment);
                getFramebufferPar3().beginRead();
                ExecutiveOrdersClient.skySampler3.set(getFramebufferPar3()::getColorAttachment);
                ExecutiveOrdersClient.renderNoSkyProgram(v);
                getFramebufferPar3().endRead();
                getFramebufferSky2().endRead();
                getFramebufferSky().endRead();
                getFramebufferSky2().clear(true);
            }


            if(player.hasStatusEffect(EOMobEffectsInit.SCRONGBONGLED_EFFECT) ) {
                if(this.scrongled<0.5f)
                    this.scrongled = Math.clamp(this.scrongled+1f/fps,0f,0.5f);
            }
            else if(this.scrongled>0.0f){
                this.scrongled = Math.clamp(this.scrongled-3f/fps,0f,0.5f);
            }
            RenderSystem.depthMask(true);
            if(MinecraftClient.getInstance().world != null)
            {
                ExecutiveOrdersClient.voidTime.set((MinecraftClient.getInstance().world.getTime()+v)/24000f);
                if(player instanceof LivingEntityAccessor accessor && accessor.getThunderedTime()>0){
                    ExecutiveOrdersClient.setBurnFadeIn((accessor.getThunderedTime()+v)/100f);
                    ExecutiveOrdersClient.renderBurnProgram(v);
                }
                ExecutiveOrdersClient.palebordTime.set((MinecraftClient.getInstance().world.getTime()+v)/24000f);
                ExecutiveOrdersClient.setBurnTime((MinecraftClient.getInstance().world.getTime()+v)/24000f);
                ExecutiveOrdersClient.setPaleTime((MinecraftClient.getInstance().world.getTime()+v)/24000f);
            }
            WorldBorder worldBorder = MinecraftClient.getInstance().world.getWorldBorder();
            float f = (float) MathHelper.clamp((12f - worldBorder.getDistanceInsideBorder(player))/12f,0f,1f);
            f = Math.max(f,this.scrongled);
            ExecutiveOrdersClient.palebordFadeIn.set(f);
            ExecutiveOrdersClient.palebordCamRot.set(1,1);
            if(f>0)
                ExecutiveOrdersClient.renderPaleBorderProgram(v);
            ExecutiveOrdersClient.paleCamRot.set(client.player.getPitch(v),((float) MathHelper.clamp((-player.getY()-64)/3f,0,0.65f)));
            float offscale2 = 0f;
            if(MinecraftClient.getInstance().world instanceof ClientWorldAccessor accessor){
                offscale2 =accessor.executiveOrders$getRipple();

            }
            float off = Math.max(((float) MathHelper.clamp((-player.getY()-53)/3f,0,0.85f)),offscale2);
            ExecutiveOrdersClient.setPaleFadeIn(off);
            if((client.player.getY()<=-51 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES))||offscale2>0){

                getFramebuffer().beginRead();
                ExecutiveOrdersClient.paleSampler.set(getFramebuffer()::getColorAttachment);
                ExecutiveOrdersClient.paleSampler2.set(MinecraftClient.getInstance().getTextureManager().getTexture(ExecutiveOrders.id("textures/effect/sculk.png")));
                ExecutiveOrdersClient.renderPaleProgram(v);
                getFramebuffer().endRead();

            }
            if((client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123) || (client.player.hasStatusEffect(EOMobEffectsInit.PHASING_EFFECT)&&!client.player.getBlockStateAtPos().isAir())||this.bongled>1){
                ExecutiveOrdersClient.roofTime.set(((MinecraftClient.getInstance().world.getTime()+v)/24000f));
                if(client.player.getBlockStateAtPos().isSolid()&&(client.player.hasStatusEffect(EOMobEffectsInit.PHASING_EFFECT))){
                   this.bongled=Math.min(this.bongled+24f/fps,20.0f);
                }
                else if(this.bongled>0){
                    this.bongled = Math.max(this.bongled-32f/fps,0);
                }
                ExecutiveOrdersClient.roofDark.set(bongled);
                ExecutiveOrdersClient.renderRoofProgram(v);
            }
            if(!client.isPaused()){
                getFramebufferPar().beginRead();
                ExecutiveOrdersClient.voidSampler2.set(getFramebufferPar2()::getColorAttachment);
                ExecutiveOrdersClient.voidSampler.set(getFramebufferPar()::getColorAttachment);
                ExecutiveOrdersClient.renderVoidParProgram(v);
                getFramebufferPar().endRead();
            }

        }
        else{
            ExecutiveOrdersClient.setPaleFadeIn(v);
        }

    }
}
