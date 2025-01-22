package net.atired.executiveorders.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Identifier;

public class ThunderedRenderEvent implements HudRenderCallback {
    private static final Identifier TEXTURE = ExecutiveOrders.id("misc/stormingoutline");
    private static final Identifier TEXTURE2 = ExecutiveOrders.id("misc/thestorm");
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if ( MinecraftClient.getInstance().player instanceof LivingEntityAccessor accessor && accessor.getThunderedTime()>0) {
            int x = drawContext.getScaledWindowWidth() / 2 - 8, y = drawContext.getScaledWindowHeight() / 2-25;
            RenderSystem.enableBlend();
            drawContext.setShaderColor(1F,1,1, accessor.getThunderedTime()/1800f);
            drawContext.drawGuiTexture(TEXTURE, 0,0,  drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight());
            if(accessor.getThunderedTime()>=97&&!accessor.isStruckDown()){
                drawContext.setShaderColor(1F,1,1, (100-accessor.getThunderedTime())/3f);
                drawContext.drawGuiTexture(TEXTURE2, 0,0,  drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight());
            }
            drawContext.setShaderColor(1F,1f,1f,1f);
            RenderSystem.disableBlend();

        }
    }
}
