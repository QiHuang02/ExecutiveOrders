package net.atired.executiveorders.mixins;

import net.atired.executiveorders.enemies.custom.IcoSphereEntity;
import net.atired.executiveorders.init.EOEntityTypeInit;
import net.atired.executiveorders.init.EOParticlesInit;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(Explosion.class)
public abstract class FeverExplosionMixin {
    @Shadow @Final private float power;

    @Shadow public abstract Vec3d getPosition();

    @Shadow @Final private World world;

    @Inject(method = "Lnet/minecraft/world/explosion/Explosion;collectBlocksAndDamageEntities()V",at = @At("HEAD"))
    private void aStarIsBorn(CallbackInfo ci){
        Vec3d pos = getPosition();
        if(world.getDimensionEntry().getKey().isPresent() && world.getDimensionEntry().getKey().get() == DimensionTypes.THE_END){

            if(this.power>1.5 && pos.getY()<world.getDimension().minY()+9&&pos.length()>2030 && pos.length()<2970 && world.getEntitiesByClass(IcoSphereEntity.class, new Box(pos, pos).expand(10), Entity::isRegionUnloaded).isEmpty()) {
                if(!world.isClient() && world instanceof ServerWorld worlder){
                    IcoSphereEntity sphere = new IcoSphereEntity(EOEntityTypeInit.ICOSPHERE,world);
                    sphere.setPos(pos.x,pos.y,pos.z);
                    sphere.setVelocity(0,4.6,0);
                    world.spawnEntity(sphere);
                    spawn2Particles(worlder, EOParticlesInit.SKY_PARTICLE,pos.getX(),-0.8,pos.getZ(),1,0.1,0.1,0.1,27);
                    spawn2Particles(worlder, EOParticlesInit.SKY_PARTICLE,pos.getX(),-0.8,pos.getZ(),1,0.1,0.1,0.1,9);
                    spawn2Particles(worlder, EOParticlesInit.SMALL_SKY_PARTICLE,pos.getX(),-0.8,pos.getZ(),30,4,4,4,0);


                }

            }
        }

    }
    @Unique
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
}
