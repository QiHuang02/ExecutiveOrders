package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Stream<TagKey<Item>> streamTags();


    @ModifyReturnValue(method = "getName()Lnet/minecraft/text/Text;",at = @At("RETURN"))
    private Text getNameEO(Text original){
        if(streamTags().toList().contains(EOEnchantmentTags.VOIDTOUCHED_TAG)){
            return original.copy().fillStyle(Style.EMPTY.withColor(0xF07DBF));
        }
        return original;
    }
}
