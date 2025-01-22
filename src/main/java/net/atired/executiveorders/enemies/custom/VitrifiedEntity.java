package net.atired.executiveorders.enemies.custom;

import net.atired.executiveorders.accessors.PersistentProjectileEntityAccessor;
import net.atired.executiveorders.enemies.goals.VitrifiedBowAttackGoal;
import net.atired.executiveorders.init.EnchantmentEffectComponentTypesInit;
import net.atired.executiveorders.init.EnchantmentInit;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VitrifiedEntity extends SkeletonEntity {
    private static final TrackedData<Integer> TARGETID;
    public int type = 0;
    private final VitrifiedBowAttackGoal<AbstractSkeletonEntity> vitrifiedBowAttackGoal = new VitrifiedBowAttackGoal<>(this, 1.0, 20, 15.0F);
    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false) {
        public void stop() {
            super.stop();
            VitrifiedEntity.this.setAttacking(false);
        }

        public void start() {
            super.start();
            VitrifiedEntity.this.setAttacking(true);
        }
    };

    @Override
    protected boolean isAffectedByDaylight() {
        return false;
    }
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack itemStack2 = this.getProjectileType(itemStack);
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        if(EnchantmentHelper.hasAnyEnchantmentsWith(this.getEquippedStack(EquipmentSlot.MAINHAND), EnchantmentEffectComponentTypesInit.ARBALEST) && persistentProjectileEntity instanceof PersistentProjectileEntityAccessor accessor)
        {
            e-=0.1;
            g*=5;
            accessor.setArbalest(2);
        }
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        persistentProjectileEntity.setCritical(true);

        this.getWorld().spawnEntity(persistentProjectileEntity);

    }
    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData);
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if(random.nextFloat() > 0.4f)
            itemStack.addEnchantment(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).get(EnchantmentInit.ARBALEST_ENCHANT_KEY)),1);
        return data;
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || (getTargetId() == -1);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if(target!=null)
            setTargetId(target.getId());
        else
            setTargetId(-1);
        super.setTarget(target);
    }
    public VitrifiedEntity(EntityType<? extends VitrifiedEntity> entityType, World world) {
        super(entityType, world);
        type = getRandom().nextBetween(0,3);
        this.updateAttackType();
    }
    protected void initGoals() {
        this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, this.getClass()));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));

    }
    protected int getHardAttackInterval() {
        return 15;
    }

    protected int getRegularAttackInterval() {
        return 30;
    }
    @Override
    public void updateAttackType() {
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.goalSelector.remove(this.meleeAttackGoal);
            this.goalSelector.remove(this.vitrifiedBowAttackGoal);
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            if (itemStack.isOf(Items.BOW)) {
                int i = this.getHardAttackInterval();
                if (this.getWorld().getDifficulty() != Difficulty.HARD) {
                    i = this.getRegularAttackInterval();
                }

                this.vitrifiedBowAttackGoal.setAttackInterval(i);
                this.goalSelector.add(4, this.vitrifiedBowAttackGoal);
            } else {

            }

        }
    }
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TARGETID, -1);
    }

    public int getTargetId() {
        return (Integer)this.getDataTracker().get(TARGETID);
    }

    public void setTargetId(int converting) {
        this.dataTracker.set(TARGETID, converting);
    }

    public static DefaultAttributeContainer.Builder createVitrifiedAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,0.3);
    }
    static {
        TARGETID = DataTracker.registerData(VitrifiedEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

}
