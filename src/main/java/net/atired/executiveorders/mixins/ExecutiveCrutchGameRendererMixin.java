package net.atired.executiveorders.mixins;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.atired.executiveorders.accessors.PaleGameRendererAccessor;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class ExecutiveCrutchGameRendererMixin implements PaleGameRendererAccessor {
    @Shadow protected abstract ShaderProgram preloadProgram(ResourceFactory factory, String name, VertexFormat format);


    @Shadow @Final private Map<String, ShaderProgram> programs;

    @Shadow protected abstract void loadPostProcessor(Identifier id);

    @Shadow private @Nullable PostEffectProcessor postProcessor;
    @Unique
    private static ShaderProgram renderTypeJellyProgram;

    @Override
    public void executiveOrders$loadMyPostProcessor(Identifier id) {
        if(id == null && this.postProcessor != null){
            this.postProcessor = null;
        }
        else if(this.postProcessor == null&&id != null){
            loadPostProcessor(id);
        }

    }

    @Redirect(method = "loadPrograms(Lnet/minecraft/resource/ResourceFactory;)V",at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;"))
    public ArrayList<Pair<ShaderProgram, Consumer<ShaderProgram>>> loadPrograms(int initialArraySize,ResourceFactory factory) throws IOException {
        ArrayList<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2 = Lists.newArrayListWithCapacity(this.programs.size());
        list2.add(Pair.of(new ShaderProgram(factory, "executive_jelly_renderer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
            renderTypeJellyProgram = program;
        }));
        return list2;
    }
}
