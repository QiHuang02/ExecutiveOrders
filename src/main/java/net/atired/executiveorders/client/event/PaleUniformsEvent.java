package net.atired.executiveorders.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.client.layers.ExecutiveRenderLayers;
import net.atired.executiveorders.init.MobEffectsInit;
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
    public static Framebuffer getFramebuffer() {
        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebuffer.setClearColor(0f, 0f, 0f, 1.0f);
        }
        return framebuffer;
    }
    private static Framebuffer framebufferPar;
    public static Framebuffer getFramebufferPar() {
        if (framebufferPar == null) {
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
        return framebufferPar2;
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
    static VertexConsumerProvider.Immediate darkImmediate = null;
    public static VertexConsumerProvider.Immediate createDarkImmediate() {
        if (darkImmediate == null) {
            SequencedMap<RenderLayer, BufferAllocator> buffers = new Object2ObjectLinkedOpenHashMap<>();
            buffers.put(RenderLayer.getEntityTranslucent(MONOLITH), new BufferAllocator(RenderLayer.getEntityTranslucent(MONOLITH).getExpectedBufferSize()));
            darkImmediate = VertexConsumerProvider.immediate(buffers,new BufferAllocator(256));
        }
        return darkImmediate;
    }
    @Override
    public void renderShaderEffects(float v) {

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {


            RenderSystem.depthMask(false);
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
            ExecutiveOrdersClient.palebordFadeIn.set(f);
            ExecutiveOrdersClient.palebordCamRot.set(1,1);
            if(f>0)
                ExecutiveOrdersClient.renderPaleBorderProgram(v);
            ExecutiveOrdersClient.paleCamRot.set(client.player.getPitch(v),((float) MathHelper.clamp((-player.getY()-64)/3f,0,0.65f)));
            ExecutiveOrdersClient.setPaleFadeIn((float) ((float) MathHelper.clamp((-player.getY()-53)/3f,0,0.85f)));
            if(client.player.getY()<=-51 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES)){

                getFramebuffer().beginRead();
                ExecutiveOrdersClient.paleSampler.set(getFramebuffer()::getColorAttachment);
                ExecutiveOrdersClient.paleSampler2.set(MinecraftClient.getInstance().getTextureManager().getTexture(ExecutiveOrders.id("textures/effect/sculk.png")));
                ExecutiveOrdersClient.renderPaleProgram(v);
                getFramebuffer().endRead();

            }
            if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){

                ExecutiveOrdersClient.renderRoofProgram(0);
            }
            getFramebufferPar().beginRead();
            ExecutiveOrdersClient.voidSampler2.set(getFramebufferPar2()::getColorAttachment);
            ExecutiveOrdersClient.voidSampler.set(getFramebufferPar()::getColorAttachment);
            ExecutiveOrdersClient.renderVoidParProgram(v);
            getFramebufferPar().endRead();
        }
        else{
            ExecutiveOrdersClient.setPaleFadeIn(0);
        }

    }
}
