package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.world.event.Vibrations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class StyleMixin {
    @ModifyReturnValue(method = "withFormatting(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;",at=@At("RETURN"))
    private Style moreFormatting(Style original,Formatting formatting){

        return original;
    }
}
