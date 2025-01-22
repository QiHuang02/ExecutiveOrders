package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.feature.BasaltColumnsFeature;
import net.minecraft.world.gen.feature.BasaltPillarFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Environment(EnvType.CLIENT)
@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyReturnValue(method = "getBrightness(Lnet/minecraft/world/dimension/DimensionType;I)F",at = @At("RETURN"))
    private static float darknessEncroaching(float original, DimensionType type, int lightLevel){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player.getY()<=-30 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES)&&!client.player.hasStatusEffect(StatusEffects.NIGHT_VISION))
        {

            float val = Math.clamp((float) ((client.player.getY()+56f)/26f),0.1f,1f);
            float copy = (original/1.1f-0.02f);
            return original*val+copy*(1-val);
        }
        return original;
    }
    @ModifyArgs(method = "update(F)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void cold(Args args){
        Color yeah = new Color(args.get(2));
        MinecraftClient client = MinecraftClient.getInstance();

        if( client.player.getY()<=-30 && (client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||client.player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES))
        {
            float val = Math.clamp((float) ((client.player.getY()+35)/5),0.4f,1f);
            args.set(2,0xFF000000| (int)(yeah.getBlue()*(1-val)+yeah.getRed()*val) << 16 | (int)((yeah.getGreen())) << 8 | (int)(yeah.getBlue()*val+yeah.getRed()*(1-val)));

        }

    }
}
