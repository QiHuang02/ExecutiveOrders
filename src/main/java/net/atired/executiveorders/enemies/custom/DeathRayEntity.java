package net.atired.executiveorders.enemies.custom;

import net.atired.executiveorders.init.EODamageTypesInit;
import net.atired.executiveorders.init.EOEntityTypeInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class DeathRayEntity extends Entity {
    private static final TrackedData<Vector3f> DIRECTION = DataTracker.registerData(DeathRayEntity.class, TrackedDataHandlerRegistry.VECTOR3F);

    private float scale = 0.0f;
    public DeathRayEntity(EntityType<?> type, World world) {
        super(type, world);
        setDir(getPos().add(0, 10, 0));
    }
    public DeathRayEntity(World world, Vec3d pos) {
        super(EOEntityTypeInit.DEATHRAY, world);
        setDir(pos.normalize().multiply(pos.length()+1));
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
    @Override
    public void tick() {
        super.tick();
        this.scale= (float) Math.sin(this.age*3.14/200)*1.5f;
        if(this.age>200){
            this.discard();
        }
        if(this.age>5){
            for(LivingEntity livingEntity : getWorld().getEntitiesByClass(LivingEntity.class,new Box(getPos(),getTargetPos().add(getPos())),LivingEntity::isLiving)){
                float length = (float) getTargetPos().length();
                float dist1 = livingEntity.distanceTo(this);
                float dist2 = (float) livingEntity.getPos().distanceTo(this.getPos().add(getTargetPos()));
                if(dist1<length+16&&dist2<length+16){
                    float mult = dist1/(dist1+dist2);
                    Vec3d accPos = getTargetPos().multiply(mult).add(this.getPos());
                    if(accPos.distanceTo(livingEntity.getPos())<2){
                        livingEntity.damage(getDamageSources().create(EODamageTypesInit.UNRAVELING),4f);
                    }
                }

            }
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return super.createSpawnPacket(entityTrackerEntry);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
    }
    public Vec3d getDir() {
        return new Vec3d(this.dataTracker.get(this.DIRECTION)).subtract(getPos()).normalize();
    }
    public Vec3d getTargetPos() {
        return new Vec3d(this.dataTracker.get(this.DIRECTION)).subtract(getPos());
    }

    public void setDir(Vec3d dir) {
        this.dataTracker.set(this.DIRECTION,dir.toVector3f());
    }

    public float getScale() {
        return scale;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DIRECTION,new Vector3f(0,1,0));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
