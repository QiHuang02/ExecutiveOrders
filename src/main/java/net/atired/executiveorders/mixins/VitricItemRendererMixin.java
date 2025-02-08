package net.atired.executiveorders.mixins;

import net.atired.executiveorders.client.ExecutiveOrdersClient;
import net.atired.executiveorders.init.EODataComponentTypeInit;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemRenderer.class)
public class VitricItemRendererMixin {
    @Unique
    private VertexConsumer consumer;
    @Inject(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",at=@At("HEAD"),cancellable = true)
    private void renderVitricItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci){
        boolean bl2;
        if (renderMode != ModelTransformationMode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            bl2 = !(block instanceof TranslucentBlock) && !(block instanceof StainedGlassPaneBlock);
        } else {
            bl2 = true;
        }
        if(bl2){
            this.consumer = stack.hasGlint() ? VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getDirectEntityGlint()),vertexConsumers.getBuffer(ExecutiveOrdersClient.VITRIC.getRenderLayer(TexturedRenderLayers.getEntityTranslucentCull()))) : vertexConsumers.getBuffer(ExecutiveOrdersClient.VITRIC.getRenderLayer(TexturedRenderLayers.getEntityTranslucentCull()));
        }
        else{
            this.consumer = null;
        }

    }
    @ModifyArgs(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"
            , at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V")
            )
    private void injected(Args args) {
        VertexConsumer vertexConsumer = args.get(5);
        ItemStack stack = args.get(1);

        if(this.consumer!=null&&ExecutiveOrdersClient.VITRIC.getProgram()!=null&&stack.get(EODataComponentTypeInit.VITRIC)!=null){
            args.set(5,this.consumer);
        }


    }
}
