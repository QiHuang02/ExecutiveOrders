package net.atired.executiveorders.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.accessors.PaleGameRendererAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.mixin.blockrenderlayer.RenderLayersMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionTypes;

public class MarkiplierEvent implements HudRenderCallback {
    private static final Identifier TEXTURE = ExecutiveOrders.id("misc/marketpliers_depth");
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

    }
}
