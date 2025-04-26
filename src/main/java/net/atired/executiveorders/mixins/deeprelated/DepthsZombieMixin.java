package net.atired.executiveorders.mixins.deeprelated;

import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public  abstract class DepthsZombieMixin extends MobEntity {
    @Shadow @Final private static Identifier REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID;

    @Shadow @Final private static EntityAttributeModifier REINFORCEMENT_CALLEE_CHARGE_REINFORCEMENT_BONUS;

    protected DepthsZombieMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",at=@At("TAIL"))
    private void depthsDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (!super.damage(source, amount)) {
            return;
        }
        if (!(this.getWorld() instanceof ServerWorld)) {
            return;
        }

    }
}
