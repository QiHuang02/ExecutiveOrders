package net.atired.executiveorders.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.init.EODataComponentTypeInit;
import net.atired.executiveorders.init.ItemsInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class PalePileDrawContextMixin {


    @Shadow public abstract void drawItem(ItemStack stack, int x, int y, int seed);

    @Shadow public abstract void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed);

    @Shadow @Final private MatrixStack matrices;

    @Shadow protected abstract void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int z);

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void draw();

    @Shadow public abstract VertexConsumerProvider.Immediate getVertexConsumers();

    @Inject(method = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",at=@At("HEAD"),cancellable = true)
    private void drawVitricItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci){
        if(stack.get(EODataComponentTypeInit.VITRIC)!=null&&stack.get(EODataComponentTypeInit.VITRIC).intValue() == 1 ){
            ci.cancel();
            Quaternionf quaternionf2 = new Quaternionf();
            RenderSystem.setShaderColor(1f,1f,1f,0.9f);
            quaternionf2.rotationZYX((float) (Math.sin(this.client.player.getWorld().getTime()/6f+(x+y)/24f)*0.05f),0, 0);
            this.matrices.multiply(quaternionf2,x+8, y+8,0);
            this.matrices.translate(0,0,40.4f);

                if (!stack.isEmpty()) {
                    BakedModel bakedModel = this.client.getItemRenderer().getModel(stack, world, entity, seed);
                    this.matrices.push();
                    this.matrices.translate((float)(x + 8), (float)(y + 8), (float)(150 + (bakedModel.hasDepth() ? z : 0)));

                    try {
                        this.matrices.scale(16.0F, -16.0F, 16.0F);
                        boolean bl = !bakedModel.isSideLit();
                        if (bl) {
                            DiffuseLighting.disableGuiDepthLighting();
                        }

                        this.client
                                .getItemRenderer()
                                .renderItem(stack, ModelTransformationMode.GUI, false, this.matrices, this.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
                        this.draw();
                        if (bl) {
                            DiffuseLighting.enableGuiDepthLighting();
                        }
                    } catch (Throwable var12) {
                        CrashReport crashReport = CrashReport.create(var12, "Rendering item");
                        CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                        crashReportSection.add("Item Type", (CrashCallable<String>)(() -> String.valueOf(stack.getItem())));
                        crashReportSection.add("Item Components", (CrashCallable<String>)(() -> String.valueOf(stack.getComponents())));
                        crashReportSection.add("Item Foil", (CrashCallable<String>)(() -> String.valueOf(stack.hasGlint())));
                        throw new CrashException(crashReport);
                    }

                    this.matrices.pop();
                }

            quaternionf2 = new Quaternionf().rotationZYX((float) -(Math.sin(this.client.player.getWorld().getTime()/6f+(x+y)/24f)*0.05f),0, 0);
            this.matrices.translate(0,0,-40.4f);
            this.matrices.multiply(quaternionf2,x+8, y+8,0);
            RenderSystem.setShaderColor(1f,1f,1f,1f);

        }
    }

        @Inject(method = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",at=@At("TAIL"),cancellable = true)
    private void drawPaleItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci){
        if(stack.getItem() == ItemsInit.PALE_PILE&&stack.get(DataComponentTypes.CONTAINER)!=null){
            ContainerComponent containerComponent = (ContainerComponent) stack.get(DataComponentTypes.CONTAINER);
            if(this.client.player!=null&&containerComponent.streamNonEmpty().count()>0&&containerComponent.streamNonEmpty().toList().getFirst().getItem()!=ItemsInit.PALE_PILE)
            {

                Quaternionf quaternionf2 = new Quaternionf();
                RenderSystem.setShaderColor(0.7f,0.7f,1f,0.5f);
                quaternionf2.rotationZYX((float) (Math.cos(this.client.player.getWorld().getTime()/8f+(x+y)/12f)*0.4f),0, 0);
                this.matrices.multiply(quaternionf2,x+8, y+8,0);
                this.matrices.scale(0.8f,0.8f,0.8f);
                this.matrices.translate(0,0,40.4f);
                drawItem(entity,entity.getWorld(),containerComponent.streamNonEmpty().toList().getFirst(), (int) (x*1.25d)+2,(int) (y*1.25d)+2,seed,0);
                quaternionf2 = new Quaternionf().rotationZYX((float) -(Math.cos(this.client.player.getWorld().getTime()/8f+(x+y)/12f)*0.4f),0, 0);
                this.matrices.translate(0,0,-40.4f);
                this.matrices.scale(1.25f,1.25f,1.25f);

                this.matrices.multiply(quaternionf2,x+8, y+8,0);

                RenderSystem.setShaderColor(1f,1f,1f,1f);
            }

        }

    }
}
