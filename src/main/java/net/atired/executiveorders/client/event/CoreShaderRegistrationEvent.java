package net.atired.executiveorders.client.event;

import net.atired.executiveorders.ExecutiveOrders;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;

public class CoreShaderRegistrationEvent implements CoreShaderRegistrationCallback {
    public static  ShaderProgram renderTypeJellyProgram = null;
    @Override
    public void registerShaders(RegistrationContext context) throws IOException {
        if(renderTypeJellyProgram==null&&MinecraftClient.getInstance().gameRenderer.getProgram("executive_jelly_renderer")!=null)
        {
            System.out.println("WOOOOHOOOOOPARTYTOTHEMEGA");
            renderTypeJellyProgram = MinecraftClient.getInstance().gameRenderer.getProgram("executive_jelly_renderer");
        }

    }
}
