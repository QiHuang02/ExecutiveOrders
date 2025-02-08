package net.atired.executiveorders.client.event;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;

public class PaleUniformsEvent implements ShaderEffectRenderCallback {
    private static Framebuffer framebuffer;
    public static Framebuffer getFramebuffer() {
        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().viewportWidth, MinecraftClient.getInstance().getFramebuffer().viewportHeight,true,false);
            framebuffer.setClearColor(0f, 0f, 0f, 1.0f);
        }
        return framebuffer;
    }
    @Override
    public void renderShaderEffects(float v) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {


            if(MinecraftClient.getInstance().world != null)
            {

                if(player instanceof LivingEntityAccessor accessor && accessor.getThunderedTime()>0){
                    ExecutiveOrdersClient.setBurnFadeIn((accessor.getThunderedTime()+v)/100f);
                    ExecutiveOrdersClient.renderBurnProgram(v);
                }

                ExecutiveOrdersClient.setBurnTime((MinecraftClient.getInstance().world.getTime()%24000+v)/24000f);
                ExecutiveOrdersClient.setPaleTime((MinecraftClient.getInstance().world.getTime()%24000+v)/24000f);
            }
            ExecutiveOrdersClient.paleCamRot.set(client.player.getPitch(v),client.player.getYaw(v));
            ExecutiveOrdersClient.setPaleFadeIn((float) ((float) MathHelper.clamp((-player.getY()-51)/10f,0,0.7f)));
            if(client.player.getY()<=-51 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES)){
                getFramebuffer().copyDepthFrom(MinecraftClient.getInstance().getFramebuffer());

                getFramebuffer().beginRead();
                ExecutiveOrdersClient.paleSampler.set(getFramebuffer()::getColorAttachment);
                ExecutiveOrdersClient.paleSampler2.set(MinecraftClient.getInstance().getTextureManager().getTexture(ExecutiveOrders.id("textures/effect/sculk.png")));
                ExecutiveOrdersClient.renderPaleProgram(v);
                getFramebuffer().endRead();


            }
            if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){

                ExecutiveOrdersClient.renderRoofProgram(0);
            }
        }
        else{
            ExecutiveOrdersClient.setPaleFadeIn(0);
        }
    }
}
