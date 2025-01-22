package net.atired.executiveorders.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.rendering.ClientWorldMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(RenderSystem.class)
public abstract class FogMixin {

    @ModifyVariable(method = "setShaderFogColor(FFF)V", at = @At("HEAD"), ordinal = 0)
    private static float endFogColor1Thingie(float value)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END && client.player.getPos().length()>9000)
        {
            return (float) Math.clamp(value+(0.8f*(Math.clamp((client.player.getPos().length()-9000f)/500f,0.0f,1f))),0,1);
        }
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){
            return 0;
        }
        return value;
    }
    @ModifyVariable(method = "setShaderFogColor(FFF)V", at = @At("HEAD"), ordinal = 1)
    private static float endFogColor2Thingie(float value)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END && client.player.getPos().length()>9000)
        {
            return (float) Math.clamp(value+(0.81f*(Math.clamp((client.player.getPos().length()-9000f)/500f,0.0f,1f))),0,1);
        }
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){
            return 0;
        }
        return value;
    }
    @ModifyVariable(method = "setShaderFogColor(FFF)V", at = @At("HEAD"), ordinal = 2)
    private static float endFogColor3Thingie(float value)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END && client.player.getPos().length()>9000)
        {
            return  (float) Math.clamp(value+(0.8f*(Math.clamp((client.player.getPos().length()-9000f)/500f,0.0f,1f))),0,1);
        }
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){
            return 0;
        }
        return value;
    }

}
