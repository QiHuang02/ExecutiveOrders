package net.atired.executiveorders.mixins.deeprelated;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class DepthsLivingEntityMixin extends Entity {
    public DepthsLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean shouldRenderName();

    @Shadow protected abstract void dropLoot(DamageSource damageSource, boolean causedByPlayer);

    @Shadow public abstract RegistryKey<LootTable> getLootTable();
    @ModifyVariable(method = "takeKnockback(DDD)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double modifyDepthsKB(double value){
        if((Entity)this instanceof ZombieEntity entity){
            if(entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant() && entity.hurtTime>2){
                return value*-0.6;
            }
        }
        return value;
    }
    @ModifyReturnValue(method = "getMovementSpeed()F",at = @At("RETURN"))
    private float getDepthsSpeed(float original){
        if((Entity)this instanceof ZombieEntity entity){
            if(entity instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant() && entity.hurtTime>2){
                return  original*2;
            }
        }
        return original;
    }

    @Shadow @Nullable protected PlayerEntity attackingPlayer;


}
