package net.atired.executiveorders.enchanments.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.networking.payloads.ExecutePayload;
import net.atired.executiveorders.networking.payloads.PreciseImpactPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record PreciseImpactEnchantmentEffect(EnchantmentLevelBasedValue amount) implements EnchantmentEntityEffect {
    public static final MapCodec<PreciseImpactEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(PreciseImpactEnchantmentEffect::amount)
    ).apply(instance, PreciseImpactEnchantmentEffect::new));
    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity target, Vec3d pos) {
        if(target instanceof LivingEntity liver && context.owner() instanceof LivingEntity owner && owner.getVelocity().length() > 0.3){
            if(target instanceof LivingEntityAccessor accessor)
                accessor.setHollowing(true);
            if((target.getWorld() instanceof ServerWorld serverWorld))
            {
                System.out.println(context.owner().getVelocity().length());
                for (ServerPlayerEntity gamer : serverWorld.getPlayers()) {

                    ServerPlayNetworking.send(gamer, new PreciseImpactPayload(target.getId(), context.owner().getVelocity().length()+0.5, owner.getRotationVector().toVector3f(),target.getEyePos().subtract(owner.getPos()).normalize().multiply(0.15+3.5*context.owner().getVelocity().length()).toVector3f()));
                }
            }
            liver.addVelocity(target.getEyePos().subtract(owner.getPos()).normalize().multiply(0.15+3.5*context.owner().getVelocity().length()));
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}
