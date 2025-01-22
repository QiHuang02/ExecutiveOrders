package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class BackGroundRendererMixin {
    @ModifyReturnValue(method = "getSkyColor(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private Vec3d getMySkyColor(Vec3d original){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){
            return Vec3d.ZERO;
        }
        return original;
    }
    @ModifyReturnValue(method = "getSkyBrightness(F)F", at = @At("RETURN"))
    private float getMySkyLight(float original){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER && client.player.getPos().y>123){
            return 0;
        }
        return original;
    }
}
