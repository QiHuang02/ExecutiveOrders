package net.atired.executiveorders.client.event;

import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;

public class PaleUniformsEvent implements ShaderEffectRenderCallback {
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

            ExecutiveOrdersClient.setPaleFadeIn((float) MathHelper.clamp((-player.getY()-30)/8f,0,0.8));
            if(client.player.getY()<=-30 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES)){
                ExecutiveOrdersClient.renderPaleProgram(v);
            }
        }
        else{
            ExecutiveOrdersClient.setPaleFadeIn(0);
        }
    }
}
