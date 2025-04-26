package net.atired.executiveorders.enemies.custom;

import net.atired.executiveorders.init.EOMobEffectsInit;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class JauntEntity extends ZombieEntity {

    private static final TrackedData<Boolean> VOLATILE = DataTracker.registerData(JauntEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public Vec3d laspos = new Vec3d(0,0,0);

    public JauntEntity(EntityType<? extends JauntEntity> entityType, World world) {
        super(entityType, world);
        setVolatile(getRandom().nextFloat() < 0.1f);
        limbAnimator.setSpeed(0.33f);
    }

    public boolean isVolatile() {
        return this.getDataTracker().get(VOLATILE);
    }
    public void setVolatile(boolean volat){
        this.getDataTracker().set(VOLATILE, volat);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VOLATILE, false);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.isRemoved()){
            this.discard();
        }
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {

        if(isVolatile())
        {
            movement = movement.rotateY((float) (Math.sin(getWorld().getTime()*4)*1.5f));
        }
        super.move(movementType, movement);
    }
    public static boolean canJauntSpawn(EntityType<? extends HostileEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos blockPos = pos.down();
        if (world.getDimension().respawnAnchorWorks()){
            return HostileEntity.canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random)&&pos.getY()>120;
        }
        return HostileEntity.canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
    }

    public float getLeaningPitch(float tickDelta) {
        return super.getLeaningPitch(tickDelta)/2f;
    }
    protected void initCustomGoals() {

        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[]{ZombifiedPiglinEntity.class}));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));

    }

    @Override
    public float getMovementSpeed() {
        if(isVolatile())
        {
            return super.getMovementSpeed()*2f;
        }
        return super.getMovementSpeed();
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {

    }
    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if(bl && target instanceof LivingEntity liver)
        {
            liver.addVelocity(new Vec3d(0,0.4,0));
        }
        if (bl && target instanceof PlayerEntity) {
            float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
            ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(EOMobEffectsInit.PHASING_EFFECT, 25 * (int)f), this);
        }
        return bl;
    }

    @Override
    public void tick() {
        if(age%15 == 0 || (isVolatile() && age%5 == 0))
        {
            laspos = getPos();
        }
        super.tick();
    }



    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HUSK_AMBIENT;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean should =super.damage(source, amount);
        if(isVolatile())
        {
            heal(20);
            setVolatile(false);
        }
        return should;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HUSK_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_HUSK_STEP;
    }

    protected boolean burnsInDaylight() {
        return false;
    }
    public static DefaultAttributeContainer.Builder createJauntAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,5.0f).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,3).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0).add(EntityAttributes.GENERIC_ARMOR, 2.0).add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    }
}
