package net.atired.executiveorders.enemies.custom;

import net.atired.executiveorders.accessors.ClientWorldAccessor;
import net.atired.executiveorders.init.EODamageTypesInit;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class IcoSphereEntity extends Entity {
    public IcoSphereEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    private static final TrackedData<Vector3f> DIRECTION = DataTracker.registerData(IcoSphereEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Integer> AGE = DataTracker.registerData(IcoSphereEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public boolean shouldRender(double distance) {
        return super.shouldRender(distance);
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(9);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.getAttacker()!=null&&source.getAttacker().distanceTo(this)<14){
            source.getAttacker().setVelocity(source.getAttacker().getPos().subtract(getPos()).multiply(0.6));
            if(getWorld().isClient()&& MinecraftClient.getInstance().player == source.getAttacker()&&getWorld() instanceof ClientWorldAccessor accessor){
                accessor.executiveOrders$setIcoPower(Math.clamp(accessor.executiveOrders$getIcoPower(),0,1));
            }

        }
        return false;
    }

    @Override
    public void tick() {
        if(getWorld() instanceof ClientWorldAccessor accessor){
            accessor.executiveOrders$setIcoPower(MathHelper.clamp(accessor.executiveOrders$getIcoPower()+0.07f,0,Math.max(1,accessor.executiveOrders$getIcoPower())));
        }
        int age = this.getAge();
        if(age%100 == 0  && !getWorld().isClient()){
            this.setDir(new Vec3d(1,0,0).rotateY((float) (Math.PI*2*getRandom().nextFloat())).rotateX((float) (Math.PI*2*(getRandom().nextFloat()-0.5f))/50));
        }
        boolean stuff = age%100 >= 48&&age%100 <= 55;
        LivingEntity livingEntity=null;
        float powah = 200;
        for(Entity entity : getWorld().getEntitiesByClass(Entity.class,getBoundingBox().expand(20),entity -> true)){
            float power = entity.distanceTo(this);
            if(power<5 && getWorld().isClient()&& MinecraftClient.getInstance().player == entity&&getWorld() instanceof ClientWorldAccessor accessor){
                accessor.executiveOrders$setIcoPower(MathHelper.clamp(accessor.executiveOrders$getIcoPower()+0.09f,0,2f));
            }
            if(power<7&&!(entity instanceof ExplosiveProjectileEntity)){
                if(entity instanceof LivingEntity living){
                    if(power<5){
                        living.damage(getDamageSources().create(EODamageTypesInit.UNRAVELING),7);
                    }
                    if(powah>power){
                        powah = power;
                        livingEntity = living;
                    }

                }
                if(stuff){
                    power = (float) Math.pow(19-power,0.5f);
                    entity.addVelocity(getPos().subtract(entity.getPos()).multiply(0.012*power));
                }
            }
        }
        if(livingEntity!=null){
            if(stuff)
                setPosition(livingEntity.getPos().subtract(getPos()).normalize().multiply(0.03*Math.pow(livingEntity.distanceTo(this),1.1)).add(getPos()));
            else
                setPosition(livingEntity.getPos().subtract(getPos()).normalize().multiply(0.03*Math.pow(livingEntity.distanceTo(this),1.1)/10).add(getPos()));
        }
        if(stuff){
            for (int i = 0; i < 4; i++) {
                getWorld().addParticle(ParticleTypes.FLAME,getParticleX(1),this.getY() +((2.0 * this.random.nextDouble() - 1.0)),getParticleZ(1),this.getDir().getX(),this.getDir().getY(),this.getDir().getZ());
            }
        }
        if(age>499){
            this.discard();
            return;
        }

        setPosition(new Vec3d(Math.sin(getAge()/20d)/4d+Math.cos(getAge()/40d)/8d,0.02,Math.cos(getAge()/20d)/4+Math.sin(getAge()/40d)/8d).multiply(0.5).multiply(Math.clamp((4-getVelocity().length())/4,0,1)).add(getVelocity().multiply(0.25)).add(getPos()));
        setVelocity(getVelocity().multiply(0.98));
        if(age%100 == 48){
            if(!getWorld().isClient()){
                BlockHitResult blockHitResult = raycast(
                        getWorld(), this,getDir(), RaycastContext.FluidHandling.NONE
                );
                DeathRayEntity deathRayEntity = new DeathRayEntity(getWorld(),blockHitResult.getPos());
                deathRayEntity.setPosition(getPos().add(0,0.5,0).add(getDir()));
                getWorld().spawnEntity(deathRayEntity);

            }
        }
        if(!getWorld().isClient())
            this.setAge(this.getAge()+1);
        super.tick();

    }
    protected static BlockHitResult raycast(World world, IcoSphereEntity icoSphereEntity, Vec3d dir, RaycastContext.FluidHandling fluidHandling) {
        Vec3d vec3d = icoSphereEntity.getPos().add(0,0.5,0).add(icoSphereEntity.getDir());
        Vec3d vec3d2 = vec3d.add(dir.multiply(80));
        return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, ShapeContext.absent()));
    }
    public Vec3d getDir() {
        return new Vec3d(this.dataTracker.get(this.DIRECTION));
    }

    public void setDir(Vec3d dir) {
        this.dataTracker.set(this.DIRECTION,dir.toVector3f());
    }

    public int getAge() {
        return this.dataTracker.get(AGE);
    }
    public void setAge(int aged){
        this.dataTracker.set(AGE,aged);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DIRECTION,new Vector3f(0,1,0));
        builder.add(AGE,0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setAge(nbt.getInt("icoSphereAge"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("icoSphereAge",getAge());
    }
}
