package net.atired.executiveorders.mixins;

import net.atired.executiveorders.accessors.ClientWorldAccessor;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ClientWorldAccessor {

    @Unique
    private float icoSpherePower = 0;
    @Unique
    private float ripple = 0;
    @Inject(method = "Lnet/minecraft/client/world/ClientWorld;tick(Ljava/util/function/BooleanSupplier;)V",at=@At("HEAD"))
    private void keeponTicking(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(executiveOrders$getIcoPower()>0){
            executiveOrders$setIcoPower(Math.max(executiveOrders$getIcoPower()-.02f,0));
        }
        if(executiveOrders$getRipple()>0){
           executiveOrders$setRipple(executiveOrders$getRipple()-0.05f);
        }
    }

    @Override
    public float executiveOrders$getIcoPower() {
        return icoSpherePower;
    }

    @Override
    public void executiveOrders$setIcoPower(float power) {
        this.icoSpherePower = power;
    }
    @Override
    public void executiveOrders$setMoreIcoPower(float power) {
        this.icoSpherePower = power;
    }

    @Override
    public float executiveOrders$getRipple() {
        return ripple;
    }

    @Override
    public void executiveOrders$setRipple(float power) {
        this.ripple= Math.clamp(power,0f,1f);
    }
}
