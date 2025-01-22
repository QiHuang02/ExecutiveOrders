package net.atired.executiveorders.mixins.deeprelated;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.init.EODataComponentTypeInit;
import net.atired.executiveorders.init.ItemsInit;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(ItemStack.class)
public class HauntedItemStackMixin {
    @ModifyReturnValue(method = "Lnet/minecraft/item/ItemStack;areItemsAndComponentsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z",at=@At("RETURN"))
    private static boolean areAxesEqual(boolean original,ItemStack stack, ItemStack otherStack){
        if(stack.isOf(otherStack.getItem())&&otherStack.getItem()== ItemsInit.HAUNTED_AXE){
            int value = stack.get(EODataComponentTypeInit.AXEHEAT);
            stack.set(EODataComponentTypeInit.AXEHEAT, otherStack.get(EODataComponentTypeInit.AXEHEAT));
            boolean yeah = Objects.equals(stack.getComponents(), otherStack.getComponents());
            stack.set(EODataComponentTypeInit.AXEHEAT,value);
            return yeah;
        }
        return original;
    }
}
