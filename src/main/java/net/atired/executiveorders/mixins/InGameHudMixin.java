package net.atired.executiveorders.mixins;

import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.dimension.DimensionTypes;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow public abstract boolean shouldShowChatDisabledScreen();

    @Shadow private int heldItemTooltipFade;
    @Shadow private ItemStack currentStack;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow @Final private MinecraftClient client;
    @Inject(method = "renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At(value = "TAIL"))
    private void injectedTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
        context.setShaderColor(1,1,1,1);
    }
    @Inject(method = "renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At(value = "HEAD"))
    private void injected(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player.getY()>126 && player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER)
        {
            float valued = (float) Math.clamp(128-player.getY(),0,2)/2f;
            context.setShaderColor(1f,0.8f*(1-valued)+1*valued,1f,1f);
            context.getMatrices().translate(Math.sin(tickCounter.getTickDelta(true)/6f+ MinecraftClient.getInstance().world.getTime()/6f)*1.9f*(1-valued), Math.cos(tickCounter.getTickDelta(true)/6f+ MinecraftClient.getInstance().world.getTime()/6f)*0.9f*(1-valued),0);

        }
        if(player.getY()<-55 && (player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD||player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD_CAVES))
        {
            float valued = 1-(float) Math.clamp(-55-player.getY(),0,8)/8f;
            context.setShaderColor(Math.clamp(valued+0.6f,0f,1f),Math.clamp(valued+0.7f,0f,1f),1f, 1f);
            context.getMatrices().translate(Math.sin(tickCounter.getTickDelta(true)/24f+ MinecraftClient.getInstance().world.getTime()/24f)*3f*(1-valued), 0,0);

        }

    }
    @Inject(method = "renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V", at = @At(value = "HEAD"),cancellable = true)
    private void tooltipped(DrawContext context, CallbackInfo ci){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        float shouldoffset = (float) (Math.sin(MinecraftClient.getInstance().world.getTime()/4f)*2f);
        if (heldItemTooltipFade > 0 && !this.currentStack.isEmpty() && (this.currentStack.streamTags().toList().contains(EOEnchantmentTags.VOIDTOUCHED_TAG)||this.currentStack.streamTags().toList().contains(EOEnchantmentTags.VITRIFIED_TAG))) {
            int l;
            MutableText mutableText = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().getFormatting());
            if (this.currentStack.contains(DataComponentTypes.CUSTOM_NAME)) {
                mutableText.formatted(Formatting.ITALIC);
            }
            int i = (int) (this.getTextRenderer().getWidth(mutableText));
            int j = (context.getScaledWindowWidth() - i) / 2;
            int k = context.getScaledWindowHeight() - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                k += 14;
            }

            if ((l = (int)((float)this.heldItemTooltipFade * 256.0f / 10.0f)) > 255) {
                l = 255;
            }
            if (l > 0 && this.currentStack.streamTags().toList().contains(EOEnchantmentTags.VOIDTOUCHED_TAG)) {
                l = l/2;
                Quaternionf quaternionf = new Quaternionf();
                quaternionf.rotationYXZ( 0f,0, (float) (Math.cos(MinecraftClient.getInstance().world.getTime()/8f)/8f));
                context.getMatrices().multiply(quaternionf,j+i/2f, k,0);
                context.getMatrices().translate(0, shouldoffset,-6);
                MutableText text = mutableText.setStyle(Style.EMPTY.withColor(0xAA338F));
                context.drawTextWithBackground(this.getTextRenderer(), text, j, k, i, ColorHelper.Argb.withAlpha(l, -1));
                context.getMatrices().translate(0, -shouldoffset,6);
                quaternionf.rotationYXZ(0,0f,-(float) (Math.cos(MinecraftClient.getInstance().world.getTime()/8f)/8f));
                context.getMatrices().multiply(quaternionf,j+i/2f, k,0);
            }
            else if (l > 0 && this.currentStack.streamTags().toList().contains(EOEnchantmentTags.VITRIFIED_TAG)) {
                Quaternionf quaternionf = new Quaternionf();
                shouldoffset = (float) (Math.sin(MinecraftClient.getInstance().world.getTime()/8f)*2f);
                quaternionf.rotationYXZ( (MinecraftClient.getInstance().world.getTime())/8f,0, 0);
                context.getMatrices().multiply(quaternionf,j+i/2f, k,0);
                MutableText text = mutableText.setStyle(Style.EMPTY.withColor(0xFFFFFF));
                context.drawTextWithBackground(this.getTextRenderer(), text, j, k, i, ColorHelper.Argb.withAlpha(l, -1));
                Quaternionf quaternionf2 = new Quaternionf();
                quaternionf2.rotationYXZ( 3.14f,0, 0);
                context.getMatrices().multiply(quaternionf2,j+i/2f, k,0);
                context.drawTextWithBackground(this.getTextRenderer(), text, j, k, i, ColorHelper.Argb.withAlpha((int)(l/2f), -1));
                quaternionf.rotationYXZ(-(MinecraftClient.getInstance().world.getTime())/8f,0,0);
                context.getMatrices().multiply(quaternionf,j+i/2f, k,0);
                quaternionf2.rotationYXZ( -3.14f,0, 0);
                context.getMatrices().multiply(quaternionf2,j+i/2f, k,0);
                ci.cancel();
                this.client.getProfiler().pop();
            }
        }
    }
}
