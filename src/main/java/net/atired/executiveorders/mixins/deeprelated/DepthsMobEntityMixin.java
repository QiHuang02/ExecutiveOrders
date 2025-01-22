package net.atired.executiveorders.mixins.deeprelated;

import net.atired.executiveorders.accessors.DepthsLivingEntityAccessor;
import net.atired.executiveorders.init.ItemsInit;
import net.atired.executiveorders.items.PalePileItem;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MobEntity.class)
public abstract class DepthsMobEntityMixin extends LivingEntity implements DepthsLivingEntityAccessor {
    @Unique
    private static final TrackedData<Boolean> EXECUTIVE_MOB_DEPTHS = DataTracker.registerData(DepthsMobEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Shadow public abstract void setUpwardSpeed(float upwardSpeed);

    private boolean radiant = false;
    protected DepthsMobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "initDataTracker",at = @At("TAIL"))
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(EXECUTIVE_MOB_DEPTHS, false);
    }
    @Override
    public float getMovementSpeed() {
        if(executiveOrders$isRadiant()){
            return super.getMovementSpeed()*1.6f;
        }
        return super.getMovementSpeed();
    }

    @Inject(method = "dropLoot",at=@At("HEAD"))
    private void test(DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci){
        if(this instanceof DepthsLivingEntityAccessor accessor && accessor.executiveOrders$isRadiant()){
            RegistryKey<LootTable> registryKey = getLootTable();
            LootTable lootTable = this.getWorld().getServer().getReloadableRegistries().getLootTable(registryKey);
            LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld)this.getWorld())).add(LootContextParameters.THIS_ENTITY, this).add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.DAMAGE_SOURCE, damageSource).addOptional(LootContextParameters.ATTACKING_ENTITY, damageSource.getAttacker()).addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, damageSource.getSource());
            if (causedByPlayer && this.attackingPlayer != null) {
                builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck()+2);
            }
            List<ItemStack> itemStacks = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
            if(!itemStacks.isEmpty()&&itemStacks.getFirst().getItem()!= Items.AIR){
                ItemStack paleOoze = new ItemStack(ItemsInit.PALE_PILE);
                ((PalePileItem)ItemsInit.PALE_PILE).addItems(paleOoze,itemStacks);
                this.dropStack(paleOoze).setNoGravity(true);
            }


        }
    }

    @Inject(method="writeCustomDataToNbt",at=@At("TAIL"))
    private void executive$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("IsDepthsRadiant", this.executiveOrders$isRadiant());
    }
    @Inject(method = "Lnet/minecraft/entity/mob/MobEntity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V",at=@At("TAIL"))
    private void executive$readData(NbtCompound nbt, CallbackInfo ci){
        this.executiveOrders$setRadiant(nbt.getBoolean("IsDepthsRadiant"));
    }
    @Override
    public int getArmor() {
        if(executiveOrders$isRadiant()){
            return super.getArmor()+7;
        }
        return super.getArmor();
    }

    @Inject(method = "initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;)Lnet/minecraft/entity/EntityData;",at = @At("TAIL"))
    public void deepFinalizeSpawning(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir){
        if(getPos().y<-30 && spawnReason != SpawnReason.REINFORCEMENT){
            if(((LivingEntity)this) instanceof HostileEntity)
            {
                this.executiveOrders$setRadiant(true);
            }
        }
    }
    @Override
    public boolean executiveOrders$isRadiant() {
        return this.dataTracker.get(EXECUTIVE_MOB_DEPTHS);
    }

    @Override
    public void executiveOrders$setRadiant(boolean radiant) {
        this.dataTracker.set(EXECUTIVE_MOB_DEPTHS,radiant);
    }
}
