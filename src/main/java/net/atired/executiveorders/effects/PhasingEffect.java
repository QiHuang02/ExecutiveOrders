package net.atired.executiveorders.effects;

import net.atired.executiveorders.init.EOMobEffectsInit;
import net.atired.executiveorders.init.EOParticlesInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class PhasingEffect extends StatusEffect {
    public PhasingEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.HARMFUL, 0xba5697);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {

        super.onRemoved(attributeContainer);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }

    @Override
    public StatusEffect fadeTicks(int fadeTicks) {
        return super.fadeTicks(fadeTicks);
    }

    // Called when the effect is applied.
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {

        Vec3d pos = entity.getPos().add(entity.getVelocity().multiply(0.2,0.1,0.2).multiply(amplifier));

            entity.noClip = false;
            if (entity.isInsideWall())
            {
                if(entity.getY()<120)
                {
                    pos = pos.add(0,-0.08,0);
                    entity.setVelocity(entity.getVelocity().multiply(1,0.9,1));
                }

                StatusEffectInstance inst = entity.getStatusEffect(EOMobEffectsInit.PHASING_EFFECT);
                inst = new StatusEffectInstance(EOMobEffectsInit.PHASING_EFFECT, inst.getDuration(), inst.getAmplifier(),inst.isAmbient(),inst.shouldShowParticles(),inst.shouldShowIcon());
                entity.setStatusEffect(inst,entity);
            }
            else if(entity.getY()<120){
                entity.setVelocity(entity.getVelocity().multiply(1,0.7,1));
                StatusEffectInstance inst = entity.getStatusEffect(EOMobEffectsInit.PHASING_EFFECT);
                inst = new StatusEffectInstance(EOMobEffectsInit.PHASING_EFFECT, Math.max(inst.getDuration()-5,0), inst.getAmplifier(),inst.isAmbient(),inst.shouldShowParticles(),inst.shouldShowIcon());
                entity.setStatusEffect(inst,entity);
            }
            if(entity.getWorld().getDimension().minY()>entity.getY()){
                if(entity.getWorld() instanceof ServerWorld serverWorld && entity instanceof ServerPlayerEntity user){
                    Vec3d vec3d2 = Vec3d.of(user.getWorld().getSpawnPos());
                    if(user.getSpawnPointPosition()!=null) {
                        vec3d2 = Vec3d.of(user.getSpawnPointPosition());
                    }
                        ServerWorld world = user.server.getWorld(user.getSpawnPointDimension());
                        user.getServerWorld().spawnParticles(EOParticlesInit.EFFIGY_PARTICLE,user.getX(),user.getBodyY(0.5),user.getZ(),1,0,0,0,0);
                        user.teleport(world,vec3d2.x,vec3d2.y,vec3d2.z,user.getYaw(),user.getPitch());
                        user.teleport(user.getParticleX(10),user.getRandomBodyY(),user.getParticleZ(10),true);
                        user.clearStatusEffects();
                        user.addStatusEffect(new StatusEffectInstance(EOMobEffectsInit.SCRONGBONGLED_EFFECT,  30, 0));

                }
            }
            entity.noClip = true;
            entity.setPos(pos.x,pos.y,pos.z);
        return super.applyUpdateEffect(entity, amplifier);
    }
}
