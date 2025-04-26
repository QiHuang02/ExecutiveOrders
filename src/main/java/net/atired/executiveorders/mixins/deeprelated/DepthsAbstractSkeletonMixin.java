package net.atired.executiveorders.mixins.deeprelated;

import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class DepthsAbstractSkeletonMixin extends LivingEntity{
    @Shadow protected abstract PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom);

    protected DepthsAbstractSkeletonMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "shootAt(Lnet/minecraft/entity/LivingEntity;F)V",at=@At("HEAD"))
    private void shootAtDepth(LivingEntity target, float pullProgress, CallbackInfo ci){
        if((LivingEntity)this instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant()){
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            ItemStack itemStack2 = this.getProjectileType(itemStack);
            for(int i = 0; i <= 1; i++){
                PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
                Vec3d dir = target.getPos().subtract(persistentProjectileEntity.getPos()).multiply(1,0,1);
                dir = new Vec3d(1,0,0).multiply(dir.length()).rotateY((float) (Math.atan2(dir.x,dir.z)+(-0.15f+i*0.3f)*3.14f)-3.14f/2);
                double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
                double d = target.getX() - this.getX();
                double f = target.getZ() - this.getZ();
                double g = Math.sqrt(d * d + f * f);
                persistentProjectileEntity.setVelocity(dir.x, e + g * 0.2F, dir.z, 1F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
                this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                this.getWorld().spawnEntity(persistentProjectileEntity);
            }
        }


    }
}
