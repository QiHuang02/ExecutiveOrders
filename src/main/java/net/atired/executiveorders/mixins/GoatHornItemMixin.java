package net.atired.executiveorders.mixins;

import net.atired.executiveorders.init.EOItemsInit;
import net.atired.executiveorders.items.PalePileItem;
import net.atired.executiveorders.items.WarHornItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Optional;

@Mixin(GoatHornItem.class)
public abstract class GoatHornItemMixin {
    @Shadow @Final private TagKey<Instrument> instrumentTag;

    @Shadow protected abstract Optional<RegistryEntry<Instrument>> getInstrument(ItemStack stack);

    @Shadow public abstract TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);

    private static void playSound(World world, PlayerEntity player, Instrument instrument) {
        SoundEvent soundEvent = instrument.soundEvent().value();
        float f = instrument.range() / 16.0F;
        world.playSoundFromEntity(player, player, soundEvent, SoundCategory.RECORDS, f*1.5f, 0.8f);
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.getPos(), GameEvent.Emitter.of(player));
    }
    @ModifyArgs(method = "playSound(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/Instrument;)V",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundFromEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private static void sound(Args args, World world, PlayerEntity player, Instrument instrument){
        if(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof WarHornItem || (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GoatHornItem)&&(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof WarHornItem)))
        {
            args.set(5,(float)args.get(5)*0.8f);
            args.set(4,(float)args.get(4)*1.5f);
        }
    }
@Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",at=@At("HEAD"),cancellable = true)
    private void usageWarHornAdd(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        if(!(user.getStackInHand(hand).getItem() instanceof WarHornItem)){
            if(user.getInventory().contains((itemStack)->{
                if(itemStack.getItem()== EOItemsInit.PALE_PILE){
                    return true;
                }
                return false;
            })){
                for (List<ItemStack> list : List.of(user.getInventory().main,user.getInventory().offHand,user.getInventory().armor)) {
                    for (ItemStack itemStack : list) {
                        if(itemStack.getItem() instanceof PalePileItem){
                            itemStack.setCount(0);
                        }
                    }
                }
                ItemStack stack = user.getStackInHand(hand).withItem(EOItemsInit.WARHORN);
                user.setStackInHand(hand,stack);
                cir.setReturnValue(TypedActionResult.consume(user.getStackInHand(hand)));
                cir.cancel();
                EOItemsInit.WARHORN.use(world,user,hand);
            };
        }
    }
}
