package net.atired.executiveorders.mixins;

import net.atired.executiveorders.init.EOParticlesInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick",at=@At("TAIL"))
    private void tickTockVoid(CallbackInfo ci){
        if(this.hasNoGravity() && this.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END && this.getWorld().isClient() && this.getVelocity().length()>0.1){
            this.getWorld().addImportantParticle(EOParticlesInit.VOID_PARTICLE,getParticleX(0.5),getY()+Math.random()/2,getParticleZ(0.5),(Math.random()-0.5)/6,-0.1,(Math.random()-0.5)/6);
        }
    }


}
