package net.atired.executiveorders.mixins;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.init.EOParticlesInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {
    @Shadow @Nullable private DamageSource lastDamageSource;

    @Shadow public abstract float getHealth();

    @Shadow protected abstract void tickHandSwing();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract int getArmor();

    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

    @Shadow protected abstract float clampScale(float scale);

    @Shadow protected abstract void initDataTracker(DataTracker.Builder builder);

    @Shadow public abstract boolean isFallFlying();

    @Shadow public abstract Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects();

    @Shadow public abstract boolean isPartOfGame();

    private int thunderedtime;
    private int executetime;
    private Vec3d oldvel = Vec3d.ZERO;
    private boolean bouncy = false;
    private boolean noclipped = false;
    private boolean doomed = false;
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Inject(at = @At("HEAD"), method = "tick()V")
    private void tickheadmix(CallbackInfo info) {
        if((Entity)this instanceof PlayerEntity gamer)
        {
            if(isStruckDown())
            {
                gamer.startFallFlying();
            }
        }
        oldvel = getVelocity();
    }
    @Inject(at = @At("TAIL"), method = "tick()V")
    private void tickmix(CallbackInfo info) {
        if((this.isFallFlying()||this.getVelocity().length()>3) && this.getPos().length()>9100 && this.getWorld().getDimensionEntry().getKey().isPresent() && this.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END)
        {
            if(getThunderedTime()<100)
            {
                setThunderedTime(getThunderedTime()+1);
                if(getThunderedTime() == 100) {
                    setstruckDown(true);
                    setThunderedTime(60);
                    if((Entity)this instanceof PlayerEntity gamer)
                    {
                        this.setVelocity(this.getVelocity().multiply(0.1));
                        this.getVelocity().add(0,-0.1,0);
                        gamer.stopFallFlying();
                    }
                    if (!this.getWorld().isClient)
                    {
                        LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT,this.getWorld());
                        lightningEntity.setPos(this.getX(),this.getY(),this.getZ());
                        this.getWorld().spawnEntity(lightningEntity);
                    }
                }
            }
        }
        else if(getThunderedTime()>0&&!isStruckDown())
        {
            setThunderedTime(getThunderedTime()-1);
        }
        if(isOnGround()&&isStruckDown())
        {
            setstruckDown(false);
        }
        if(isHollowing()) {
            Vec3d vel = getVelocity();
            Vec3d vec3 = this.adjustMovementForCollisions(vel);
            boolean flagx = !MathHelper.approximatelyEquals(vel.x, vec3.x);
            boolean flagz = !MathHelper.approximatelyEquals(vel.z, vec3.z);

            if (flagx) {
                this.setOnGround(false);
                this.setVelocity(-oldvel.x, oldvel.y, oldvel.z);
            }
            if (flagz) {
                this.setOnGround(false);
                this.setVelocity(getVelocity().x, oldvel.y, -oldvel.z);
            }
            if(getVelocity().length() > 0.8) {
                List<LivingEntity> list = getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(0.1), LivingEntity::canTakeDamage);
                for (LivingEntity a : list) {
                    if (!a.equals(this) && a.hurtTime == 0) {
                        a.damage(getDamageSources().flyIntoWall(), (float) getVelocity().length() * 2+2);
                        a.addVelocity(getVelocity().multiply(1.2));
                        if(getWorld() instanceof ServerWorld servo)
                        {
                            servo.spawnParticles(ParticleTypes.CRIT,a.getEyePos().x,a.getEyePos().y,a.getEyePos().z,1,0,0,0,0);
                        }
                    }
                }
            }
            if (this.groundCollision && vel.length() > 0.2) {
                this.setOnGround(false);
                this.setVelocity(new Vec3d(getVelocity().x, -oldvel.y * 1.2, getVelocity().z));
            }

            this.setVelocity(this.getVelocity().multiply(1.03, 1, 1.03));
            if(vel.length() < 0.2)
                setHollowing(false);
            else if(horizontalCollision||verticalCollision)
            {
                damage(getDamageSources().flyIntoWall(), (float) getVelocity().length()*5);
            }
        }
        if(this instanceof LivingEntityAccessor accessor)
        {
            if(accessor.getExecuteTime() > 0)
            {
                accessor.setExecuteTime(accessor.getExecuteTime()-1);
                if(accessor.getExecuteTime() == 0){
                    this.damage(lastDamageSource,getHealth()*2);
                    if(getWorld() instanceof ServerWorld servo)
                    {
                        servo.spawnParticles(EOParticlesInit.EXECUTE_PARTICLE,getEyePos().x,getEyePos().y,getEyePos().z,1,0,0,0,0);
                    }
                }
            }

        }
    }
    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.getWorld().getEntityCollisions(this, box.stretch(movement));
        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.getWorld(), list);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl4 = bl2 && movement.y < 0.0;
        if (this.getStepHeight() > 0.0F && (bl4 || this.isOnGround()) && (bl || bl3)) {
            Box box2 = bl4 ? box.offset(0.0, vec3d.y, 0.0) : box;
            Box box3 = box2.stretch(movement.x, (double)this.getStepHeight(), movement.z);
            if (!bl4) {
                box3 = box3.stretch(0.0, -9.999999747378752E-6, 0.0);
            }

            List<VoxelShape> list2 = findCollisionsForMovement(this, getWorld(), list, box3);
            float f = (float)vec3d.y;
            float[] fs = collectStepHeights(box2, list2, this.getStepHeight(), f);
            float[] var14 = fs;
            int var15 = fs.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                float g = var14[var16];
                Vec3d vec3d2 = adjustMovementForCollisions(new Vec3d(movement.x, (double)g, movement.z), box2, list2);
                if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                    double d = box.minY - box2.minY;
                    return vec3d2.add(0.0, -d, 0.0);
                }
            }
        }

        return vec3d;
    }
    private static List<VoxelShape> findCollisionsForMovement(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(regularCollisions.size() + 1);
        if (!regularCollisions.isEmpty()) {
            builder.addAll(regularCollisions);
        }

        WorldBorder worldBorder = world.getWorldBorder();
        boolean bl = entity != null && worldBorder.canCollide(entity, movingEntityBoundingBox);
        if (bl) {
            builder.add(worldBorder.asVoxelShape());
        }

        builder.addAll(world.getBlockCollisions(entity, movingEntityBoundingBox));
        return builder.build();
    }
    private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return movement;
        } else {
            double d = movement.x;
            double e = movement.y;
            double f = movement.z;
            if (e != 0.0) {
                e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, e);
                if (e != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
                }
            }

            boolean bl = Math.abs(d) < Math.abs(f);
            if (bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
                if (f != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
                }
            }

            if (d != 0.0) {
                d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, d);
                if (!bl && d != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
                }
            }

            if (!bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
            }

            return new Vec3d(d, e, f);
        }
    }
    private static float[] collectStepHeights(Box collisionBox, List<VoxelShape> collisions, float f, float stepHeight) {
        FloatSet floatSet = new FloatArraySet(4);
        Iterator var5 = collisions.iterator();

        while(var5.hasNext()) {
            VoxelShape voxelShape = (VoxelShape)var5.next();
            DoubleList doubleList = voxelShape.getPointPositions(Direction.Axis.Y);
            DoubleListIterator var8 = doubleList.iterator();

            while(var8.hasNext()) {
                double d = (Double)var8.next();
                float g = (float)(d - collisionBox.minY);
                if (!(g < 0.0F) && g != stepHeight) {
                    if (g > f) {
                        break;
                    }

                    floatSet.add(g);
                }
            }
        }

        float[] fs = floatSet.toFloatArray();
        FloatArrays.unstableSort(fs);
        return fs;
    }
    @Override
    public void setExecuteTime(int numb) {
        this.executetime = numb;
    }
    @Override
    public int getExecuteTime()
    {
        return this.executetime;
    }

    @Override
    public void setNoclipped(boolean noclipped) {
        this.noclipped = noclipped;
    }

    @Override
    public boolean isNoclipped() {
        return this.noclipped;
    }

    @Override
    public void setThunderedTime(int numb) {
        this.thunderedtime = numb;
    }
    @Override
    public int getThunderedTime()
    {
        return this.thunderedtime;
    }
    @Override
    public void setHollowing(boolean hollowing) {
        this.bouncy = hollowing;
    }
    @Override
    public boolean isHollowing() {
        return this.bouncy;
    }
    @Override
    public void setstruckDown(boolean hollowing) {
        this.doomed = hollowing;
    }
    @Override
    public boolean isStruckDown() {
        return this.doomed;
    }
}
