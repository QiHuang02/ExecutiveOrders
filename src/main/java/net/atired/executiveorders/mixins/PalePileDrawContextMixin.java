package net.atired.executiveorders.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.init.ItemsInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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

    @Inject(method = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",at=@At("TAIL"))
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
