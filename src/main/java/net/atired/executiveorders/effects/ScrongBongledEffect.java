package net.atired.executiveorders.effects;

import net.atired.executiveorders.init.EOMobEffectsInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class ScrongBongledEffect extends StatusEffect {
    public ScrongBongledEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.HARMFUL, 0x000000);
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

        return super.applyUpdateEffect(entity, amplifier);
    }
}
