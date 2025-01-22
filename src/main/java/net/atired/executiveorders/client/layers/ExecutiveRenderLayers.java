package net.atired.executiveorders.client.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ExecutiveRenderLayers extends RenderPhase {
    public ExecutiveRenderLayers(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }
    public static final ShaderProgram EXECUTIVE_JELLY_RENDER_PROGRAM = new ShaderProgram(ExecutiveRenderLayers::getRenderTypeJellyProgram);
    private static final Function<Identifier, RenderLayer> EXECUTIVE_JELLY_RENDER = Util.memoize(texture -> {

        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().program(EXECUTIVE_JELLY_RENDER_PROGRAM).texture(new RenderPhase.Texture((Identifier)texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("executive_jelly_renderer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters);
    });
    public static net.minecraft.client.gl.ShaderProgram getRenderTypeJellyProgram() {
        if(MinecraftClient.getInstance().gameRenderer.getProgram("executive_jelly_renderer")!=null){
            return MinecraftClient.getInstance().gameRenderer.getProgram("executive_jelly_renderer");
        }

        return (GameRenderer.getRenderTypeEntityTranslucentCullProgram());
    }
    public static RenderLayer getExecutiveJelly(Identifier texture) {

        return EXECUTIVE_JELLY_RENDER.apply(texture);
    }
}
