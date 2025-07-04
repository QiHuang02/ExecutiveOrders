package net.atired.executiveorders.mixins;

import net.atired.executiveorders.init.EOParticlesInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public abstract class EntityMoveMixin {
    @Shadow public abstract Vec3d getPos();

    @Shadow public abstract World getWorld();

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract double getX();

    @Shadow public abstract float getYaw(float tickDelta);

    @Shadow public abstract double getY();

    @Shadow public abstract double getLerpTargetZ();

    @Shadow public abstract double getZ();

    @ModifyVariable(method = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), argsOnly = true)
    private Vec3d moveMix(Vec3d args){
        if(this.getPos().length()>2030 && this.getPos().length()<2970 && this.getWorld().getDimensionEntry().getKey().isPresent() && this.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END)
        {

            if(this.getPos().getY()+(args).getY()<0){
                if(((Object)this) instanceof EndermanEntity){
                    return args;
                }
                if(((Object)this) instanceof PlayerEntity entity && entity.isSneaking()){
                    return args.multiply(1,0,1).add(0,-this.getPos().getY(),0);
                }
                getWorld().addParticle(EOParticlesInit.SKY_PARTICLE,getX(),getY(),getZ(),0,0,0);
                double y = getVelocity().getY();
                setVelocity(getVelocity().multiply(1,y/Math.abs(y)*1.5,1));
                return args.add(0,-this.getPos().getY()-args.y*3,0);
            }
        }
        return args;
    }
}
