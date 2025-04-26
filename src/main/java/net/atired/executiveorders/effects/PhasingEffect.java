package net.atired.executiveorders.effects;

import net.atired.executiveorders.init.EOMobEffectsInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

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

        Vec3d pos = entity.getPos().add(entity.getVelocity().multiply(0.2,0.1,0.2).multiply(amplifier));
        if(entity instanceof PlayerEntity) {
            entity.noClip = false;
            if (entity.isInsideWall())
            {
                pos = pos.add(0,-0.04,0);
                StatusEffectInstance inst = entity.getStatusEffect(EOMobEffectsInit.PHASING_EFFECT);
                inst = new StatusEffectInstance(EOMobEffectsInit.PHASING_EFFECT, inst.getDuration()+1, inst.getAmplifier(),inst.isAmbient(),inst.shouldShowParticles(),inst.shouldShowIcon());
                entity.addStatusEffect(inst);
            }
            else if(entity.getY()<120){
                entity.setVelocity(entity.getVelocity().multiply(0.9));
                StatusEffectInstance inst = entity.getStatusEffect(EOMobEffectsInit.PHASING_EFFECT);
                inst = new StatusEffectInstance(EOMobEffectsInit.PHASING_EFFECT, inst.getDuration()-1, inst.getAmplifier(),inst.isAmbient(),inst.shouldShowParticles(),inst.shouldShowIcon());
                entity.addStatusEffect(inst);
            }
            entity.noClip = true;
            entity.setPos(pos.x,pos.y,pos.z);
        }
        return super.applyUpdateEffect(entity, amplifier);
    }
}
