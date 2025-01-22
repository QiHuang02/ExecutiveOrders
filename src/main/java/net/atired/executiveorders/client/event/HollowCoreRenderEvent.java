package net.atired.executiveorders.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Identifier;

public class HollowCoreRenderEvent implements HudRenderCallback {
    private static final Identifier TEXTURE = ExecutiveOrders.id("hud/hollow_core_notif");
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        if (EnchantmentHelper.hasAnyEnchantmentsIn(MinecraftClient.getInstance().player.getMainHandStack(), EOEnchantmentTags.HOLLOW_CORE_TAG) && MinecraftClient.getInstance().player.getVelocity().length() > 0.3) {
            int x = drawContext.getScaledWindowWidth() / 2 - 8, y = drawContext.getScaledWindowHeight() / 2-25;
            RenderSystem.enableBlend();

            drawContext.setShaderColor(1F,1-(float) Math.clamp(MinecraftClient.getInstance().player.getVelocity().length(),0,1),1-(float) Math.clamp(MinecraftClient.getInstance().player.getVelocity().length(),0,1), 1);
            drawContext.drawGuiTexture(TEXTURE, x, y, 16, 16);
            drawContext.setShaderColor(1F,1f,1f,1f);
            RenderSystem.disableBlend();

        }
    }
}
