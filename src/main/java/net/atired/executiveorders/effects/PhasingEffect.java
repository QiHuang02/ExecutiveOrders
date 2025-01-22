package net.atired.executiveorders.effects;

import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.init.MobEffectsInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class PhasingEffect extends StatusEffect {
    public PhasingEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.NEUTRAL, 0xba5697);
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

        Vec3d pos = entity.getPos().add(entity.getVelocity().multiply(0.2,0.8,0.2).multiply(amplifier));
        if(entity instanceof PlayerEntity) {
            entity.noClip = false;
            if (entity.isInsideWall())
            {
                pos = pos.add(0,-0.16,0);
                StatusEffectInstance inst = entity.getStatusEffect(MobEffectsInit.PHASING_EFFECT);
                inst = new StatusEffectInstance(MobEffectsInit.PHASING_EFFECT, inst.getDuration()+1, inst.getAmplifier(),inst.isAmbient(),inst.shouldShowParticles(),inst.shouldShowIcon());
                entity.addStatusEffect(inst);
            }
            else{
                entity.setVelocity(entity.getVelocity().multiply(0.94));
            }
            entity.noClip = true;
            entity.setPos(pos.x,pos.y,pos.z);
        }
        return super.applyUpdateEffect(entity, amplifier);
    }
}
