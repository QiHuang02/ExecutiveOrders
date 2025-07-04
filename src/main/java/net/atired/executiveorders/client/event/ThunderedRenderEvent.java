package net.atired.executiveorders.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.init.EOMobEffectsInit;
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

    }
}
