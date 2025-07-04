package net.atired.executiveorders.mixins;

import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;
import java.util.List;

@Mixin(ServerPlayerEntity.class)
public class EODeathMessageServerPlayerMixin {

    @ModifyVariable(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V",at = @At("STORE"),ordinal = 0)
    private Text modifyMessage(Text message){
        if(message.getString().contains("!(wrongfont)")){
            Style style= message.getStyle();
            List<String> stringList = Arrays.stream(message.getString().split(" ")).toList();
            int index = stringList.indexOf("!(wrongfont)");
            String middle = stringList.get(index+1);
            String first = "";
            for (int i = 0; i < index; i++) {
                first = first + stringList.get(i) + " ";
            }
            String last = " ";
            if(stringList.size()>index+2){
                for (int i = index+2; i < stringList.size(); i++) {
                    last = last + stringList.get(i) + " ";
                }
            }

            Text texted = Text.literal(middle).getWithStyle(style.withFont(ExecutiveOrders.id("wrong"))).getFirst();
            message = Text.literal(first).getWithStyle(style).getFirst().copy().append(texted);
            message = message.copy().append(Text.literal(last).getWithStyle(style).getFirst());

        }
        return message;
    }
}
