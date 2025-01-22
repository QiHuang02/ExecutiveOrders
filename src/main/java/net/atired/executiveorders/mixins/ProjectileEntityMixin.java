package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.accessors.PersistentProjectileEntityAccessor;
import net.atired.executiveorders.networking.payloads.ArbalestPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity implements PersistentProjectileEntityAccessor {
    @Shadow public abstract void setDamage(double damage);

    @Shadow public abstract double getDamage();

    private float scale = 1;
    private int isArbalest = 0;

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public int getArbalest() {
        return isArbalest;
    }

    @Override
    public void setArbalest(int arbalest) {
        isArbalest = arbalest;
    }

    @Override
    public void setProjScale(float numb) {
        scale = numb;
    }
    @ModifyReturnValue(method = "getGravity", at = @At("RETURN"))
    private double getGravity$tyumers(double original){
        if(this instanceof PersistentProjectileEntityAccessor accessor && accessor.getProjScale() > 1)
        {
            return original*10;
        }
        return original;
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void mixTick(CallbackInfo ci){
        if(this.isArbalest >= 2 && !isRegionUnloaded())
        {
            setArbalest(1);
            setProjScale(3);
            setDamage(getDamage()*2);
            if((getWorld() instanceof ServerWorld serverWorld))
            {
                for (ServerPlayerEntity gamer : serverWorld.getPlayers()) {
                    ServerPlayNetworking.send(gamer, new ArbalestPayload(getId(),3));
                }
            }
        }
    }
    @Inject(method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V", at = @At("HEAD"))
    private void onhit$arbalestdelish(EntityHitResult entityHitResult, CallbackInfo ci){
        if(this instanceof PersistentProjectileEntityAccessor accessor && accessor.getProjScale()>1)
            entityHitResult.getEntity().addVelocity(this.getVelocity().multiply(2));
    }
    @Inject(method = "writeCustomDataToNbt",at = @At("TAIL"))
    protected void writeCustomDataToNbt$arbalestyummers(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("IsArbalest", getArbalest());
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    protected void readCustomDataFromNbt$arbalestyummers(NbtCompound nbt, CallbackInfo ci) {
        setArbalest(nbt.getInt("IsArbalest"));
        if(getArbalest() != 0)
        {
            System.out.println("WHYWHYWHYGODDAMNITWHYFUCK");
            setArbalest(2);
        }
    }
    @Override
    public float getProjScale() {
        return scale;
    }
}
