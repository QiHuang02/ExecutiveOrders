package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Environment(EnvType.CLIENT)
@Mixin(LightmapTextureManager.class)
public class LightTextureMixin {
    @ModifyReturnValue(
            method = {"getBrightness(Lnet/minecraft/world/dimension/DimensionType;I)F"},
            at = @At(value = "RETURN")
    )
    private static float getBrightness(float original) {
        if(MinecraftClient.getInstance().player != null)
        {
            ClientPlayerEntity gamer = MinecraftClient.getInstance().player;
            if(gamer instanceof LivingEntityAccessor accessor && accessor.getThunderedTime()>95)
            {
                return original*8f;
            }
        }
        return original;
    }
}
