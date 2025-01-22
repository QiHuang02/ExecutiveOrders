package net.atired.executiveorders.mixins;

import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "startFallFlying()V",cancellable = true)
    private void nuhuhflying(CallbackInfo info) {
        if(this instanceof LivingEntityAccessor accessor&&accessor.isStruckDown()){
            info.cancel();
        }
    }
}
