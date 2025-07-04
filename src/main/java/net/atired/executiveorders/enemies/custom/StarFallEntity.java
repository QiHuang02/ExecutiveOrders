package net.atired.executiveorders.enemies.custom;


import net.atired.executiveorders.init.EODamageTypesInit;
import net.atired.executiveorders.init.EOParticlesInit;
import net.atired.executiveorders.particles.custom.EffigyParticle;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.minecraft.world.World;

public class StarFallEntity extends Entity {
    private Vec3d[] trailPositions = new Vec3d[64];
    protected static final TrackedData<Float> DATA_SCALE;
    private int trailPointer = -1;
    private Vec3d lastPos;
    public StarFallEntity(EntityType<?> p_19870_, World p_19871_) {
        super(p_19870_, p_19871_);
        setRenderDistanceMultiplier(4);
        this.ignoreCameraFrustum = true;
        this.setVelocity(Math.random()*0.25+0.25,-2,Math.random()*0.25+0.25);
        this.lastPos = new Vec3d(0,0,0);
        this.dataTracker.set(DATA_SCALE,0.55F);
    }


    public Vec3d getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3d d0 = this.trailPositions[j];
        Vec3d d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.multiply(partialTick));
    }
    public boolean hasTrail() {
        return trailPointer != -1;
    }

    public float getRandumb()
    {
        return this.dataTracker.get(DATA_SCALE);
    }

    protected boolean canHitEntity(Entity p_37250_) {
        return p_37250_.canBeHitByProjectile();
    }
    protected void onHit(HitResult hitResult) {
        HitResult.Type hitresult$type = hitResult.getType();
        Vec3d movement = this.getVelocity();

        if (hitresult$type == HitResult.Type.BLOCK) {
            Box ab=this.getBoundingBox().expand(6);
            int xm = MathHelper.floor(ab.minX);
            int ym = MathHelper.floor(ab.minY);
            int zm = MathHelper.floor(ab.minZ);
            int xa = MathHelper.floor(ab.maxX);
            int ya = MathHelper.floor(ab.maxY);
            int za = MathHelper.floor(ab.maxZ);
            for(int x = xm; x<=xa;x+=1)
            {
                for(int y = ym; y<=ya;y+=1)
                {
                    for(int z = zm; z<=za;z+=1)
                    {
                        BlockPos blockPos = new BlockPos(x,y,z);
                        if(!getWorld().getBlockState(blockPos).isAir()&&getWorld().getBlockState(blockPos.up()).isAir()&&blockPos.toCenterPos().distanceTo(getPos())<3+Math.random()*2)
                        {

                            double speed = MathHelper.clamp(5-blockPos.toCenterPos().distanceTo(getPos()),0,3)+Math.random();
                            speed /= 2;
                            this.getWorld().addImportantParticle(new BlockStateParticleEffect(EOParticlesInit.SANDED_PARTICLE,getWorld().getBlockState(blockPos)),true,blockPos.getX()+Math.random()/4,blockPos.getY()+0.7,blockPos.getZ()+Math.random()/4,-getVelocity().getX(),speed,-getVelocity().getZ());

                        }

                    }
                }
            }
            BlockHitResult blockhitresult = (BlockHitResult)hitResult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            Vec3d blockHitVec = blockhitresult.getPos();
            spawnRandomItem(blockHitVec,movement);
            if(!getWorld().isClient()){
                dataTracker.set(DATA_SCALE,2f);
                setVelocity(getVelocity().multiply(0.1));
            }

        }
    }
    private void spawnRandomItem(Vec3d pos, Vec3d motion)
    {
        double random = getRandom().nextDouble();
        if(random<0.01)
        {

            ItemEntity itemEntity = new ItemEntity(this.getWorld(),pos.x,pos.y,pos.z,new ItemStack(Items.END_STONE,2),motion.x,motion.y,motion.z);

            itemEntity.setPickupDelay(2);

            this.getWorld().spawnEntity(itemEntity);
        }

    }
    @Override
    public void tick() {
        super.tick();
        if(getRandumb()>=2f && getWorld() instanceof ServerWorld serverWorld){

            for(LivingEntity entity : getWorld().getEntitiesByClass(LivingEntity.class,new Box(getBlockPos()).expand(3),LivingEntity::isLiving)){
                entity.damage(getDamageSources().create(EODamageTypesInit.STARSTRUCK),6);

                entity.addVelocity(0,0.1,0);

            }


            spawnRandomItem(getPos().add(0,4,0),getVelocity().multiply(-1));
            for(int samples = 0; samples < 6; samples++) {
                Vec3d pos = getTrailPosition(samples,0);
                serverWorld.spawnParticles(EOParticlesInit.SMALL_SKY_PARTICLE,pos.getX(),pos.getY(),pos.getZ(),samples,samples/12f,samples/12f,samples/12f,0);
            }
            this.discard();
        }
        this.checkBlockCollision();
        BlockState state = this.getWorld().getBlockState(this.getBlockPos());
        HitResult hitresult = ProjectileUtil.getCollision(this,this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS ) {
            this.onHit(hitresult);
        }
        this.setVelocity(this.getVelocity().multiply(1.004f).add(0,-0.005,0));
        Vec3d vec3 = this.getVelocity();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        this.setPos(d2, d0, d1);
        this.lastPos = this.getPos();
        Vec3d trailAt = this.getPos().add(0, this.getHeight()/2, 0);
        if (trailPointer == -1) {
            Vec3d backAt = trailAt;
            for (int i = 0; i < trailPositions.length; i++) {
                trailPositions[i] = backAt;
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
        if(this.getVelocity().y>0.1 || this.getPos().y<=0){
            if(this.getWorld() instanceof ServerWorld world){
                spawn2Particles(world,EOParticlesInit.SMALL_SKY_PARTICLE,getX(),getY(),getZ(),2,1,0.25,1,0.1);
                spawn2Particles(world,EOParticlesInit.SKY_PARTICLE,getX(),-0.8,getZ(),1,0.05,0.05,0.05,0);

            }
            this.discard();
        }
    }
    public <T extends ParticleEffect> int spawn2Particles(ServerWorld world, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, true, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int i = 0;
        for (int j = 0; j < world.getPlayers().size(); ++j) {
            ServerPlayerEntity serverPlayerEntity = world.getPlayers().get(j);
            if (!world.sendToPlayerIfNearby(serverPlayerEntity, true, x, y, z, particleS2CPacket)) continue;
            ++i;
        }
        return i;
    }
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.getInt("shouldPerish")==1)
            dataTracker.set(DATA_SCALE,2f);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("shouldPerish",1);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DATA_SCALE,1F);
    }


    static {
        DATA_SCALE = DataTracker.registerData(StarFallEntity.class, TrackedDataHandlerRegistry.FLOAT);

    }


}
