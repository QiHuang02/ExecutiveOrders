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
        ServerWorld serverWorld = (ServerWorld)this.getWorld();
        LivingEntity livingEntity = getTarget();
        if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
            livingEntity = (LivingEntity)source.getAttacker();
        }
        if(livingEntity == null || !((LivingEntity)this instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant())) {
            return;
        }
        for(int ol = 0; ol < 20; ol++)
        {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            ZombieEntity zombieEntity = new ZombieEntity(this.getWorld());
            for (int l = 0; l < 50; ++l) {
                int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                BlockPos blockPos = new BlockPos(m, n, o);
                EntityType<?> entityType = zombieEntity.getType();
                if (!SpawnRestriction.isSpawnPosAllowed(entityType, this.getWorld(), blockPos) || !SpawnRestriction.canSpawn(entityType, serverWorld, SpawnReason.REINFORCEMENT, blockPos, this.getWorld().random)) continue;
                zombieEntity.setPosition(m, n, o);
                if (this.getWorld().isPlayerInRange(m, n, o, 7.0) || !this.getWorld().doesNotIntersectEntities(zombieEntity) || !this.getWorld().isSpaceEmpty(zombieEntity) || this.getWorld().containsFluid(zombieEntity.getBoundingBox())) continue;
                zombieEntity.setTarget(livingEntity);
                zombieEntity.initialize(serverWorld, this.getWorld().getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.REINFORCEMENT, null);
                serverWorld.spawnEntityAndPassengers(zombieEntity);
                EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
                EntityAttributeModifier entityAttributeModifier = entityAttributeInstance.getModifier(REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID);
                double d = entityAttributeModifier != null ? entityAttributeModifier.value() : 0.0;
                entityAttributeInstance.removeModifier(REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID);
                entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID, d - 0.01, EntityAttributeModifier.Operation.ADD_VALUE));
                zombieEntity.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(REINFORCEMENT_CALLEE_CHARGE_REINFORCEMENT_BONUS);
                break;
            }

        }

    }
}
