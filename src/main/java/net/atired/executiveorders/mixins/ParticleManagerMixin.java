package net.atired.executiveorders.mixins;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.client.event.PaleUniformsEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Shadow @Final private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;
    @Shadow @Final private Map<ParticleTextureSheet, Queue<Particle>> particles;
    @Shadow @Final private TextureManager textureManager;
    @Inject(method = "renderParticles",at= @At(value = "TAIL"))
    private void addToList(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci){
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        lightmapTextureManager.enable();
        RenderSystem.enableDepthTest();
        PaleUniformsEvent.getFramebufferPar().beginWrite(false);
            ParticleTextureSheet sheet = ExecutiveOrdersClient.PARTICLE_SHEET_VOID;
            Queue<Particle> queue = (Queue<Particle>)this.particles.get(sheet);
            if (queue != null && !queue.isEmpty()) {
                RenderSystem.setShader(GameRenderer::getParticleProgram);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = sheet.begin(tessellator, this.textureManager);
                if (bufferBuilder != null) {
                    for (Particle particle : queue) {
                        try {
                            particle.buildGeometry(bufferBuilder, camera, tickDelta);
                        } catch (Throwable var14) {
                            CrashReport crashReport = CrashReport.create(var14, "Rendering Particle");
                            CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                            crashReportSection.add("Particle", particle::toString);
                            crashReportSection.add("Particle Type", sheet::toString);
                            throw new CrashException(crashReport);
                        }
                    }

                    BuiltBuffer builtBuffer = bufferBuilder.endNullable();
                    if (builtBuffer != null) {
                        BufferRenderer.drawWithGlobalProgram(builtBuffer);
                    }
                }
            }
        PaleUniformsEvent.getFramebufferPar().endWrite();
        PaleUniformsEvent.getFramebufferPar3().beginWrite(false);
        sheet = ExecutiveOrdersClient.PARTICLE_SHEET_SKY;
        queue = (Queue<Particle>)this.particles.get(sheet);
        if (queue != null && !queue.isEmpty()) {
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = sheet.begin(tessellator, this.textureManager);
            if (bufferBuilder != null) {
                for (Particle particle : queue) {
                    try {
                        particle.buildGeometry(bufferBuilder, camera, tickDelta);
                    } catch (Throwable var14) {
                        CrashReport crashReport = CrashReport.create(var14, "Rendering Particle");
                        CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                        crashReportSection.add("Particle", particle::toString);
                        crashReportSection.add("Particle Type", sheet::toString);
                        throw new CrashException(crashReport);
                    }
                }

                BuiltBuffer builtBuffer = bufferBuilder.endNullable();
                if (builtBuffer != null) {
                    BufferRenderer.drawWithGlobalProgram(builtBuffer);
                }
            }
        }
        PaleUniformsEvent.getFramebufferPar3().endWrite();
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightmapTextureManager.disable();
    }

}
