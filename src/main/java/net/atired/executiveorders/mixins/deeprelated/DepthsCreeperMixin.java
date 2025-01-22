package net.atired.executiveorders.mixins.deeprelated;

import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class DepthsCreeperMixin extends MobEntity {
    @Shadow protected abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    @Shadow public abstract void setFuseSpeed(int fuseSpeed);

    @Shadow public abstract int getFuseSpeed();

    @Shadow protected abstract void spawnEffectsCloud();

    @Shadow private int explosionRadius;

    @Shadow public abstract boolean shouldRenderOverlay();

    @Shadow private int currentFuseTime;

    protected DepthsCreeperMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "explode",at=@At("HEAD"),cancellable = true)
    private void executive$explode(CallbackInfo ci){
        if((LivingEntity)this instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant() && this.getHealth()>5){
            ci.cancel();
            this.setHealth(this.getHealth()-6);
            this.currentFuseTime = -1;
            if (!this.getWorld().isClient) {
                float f = this.shouldRenderOverlay() ? 2.0f : 1.0f;
                this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f*0.9f, World.ExplosionSourceType.MOB);
                this.spawnEffectsCloud();
            }
        }
    }
}
