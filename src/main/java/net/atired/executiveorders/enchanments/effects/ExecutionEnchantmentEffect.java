package net.atired.executiveorders.enchanments.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.networking.ExecutiveOrdersNetworkingConstants;
import net.atired.executiveorders.networking.payloads.ExecutePayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record ExecutionEnchantmentEffect(EnchantmentLevelBasedValue amount) implements EnchantmentEntityEffect {
    public static final MapCodec<ExecutionEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(ExecutionEnchantmentEffect::amount)
    ).apply(instance,ExecutionEnchantmentEffect::new));
    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity target, Vec3d pos) {
        if(target instanceof LivingEntity liver && context.owner() instanceof PlayerEntity player){
            if(liver.getHealth()/liver.getMaxHealth()<0.2*level && liver instanceof LivingEntityAccessor accessor)
            {
                if((player.getWorld() instanceof ServerWorld serverWorld))
                {
                    for (ServerPlayerEntity gamer : PlayerLookup.tracking(liver)) {
                        ServerPlayNetworking.send(gamer, new ExecutePayload(target.getId()));
                    }
                }
                accessor.setExecuteTime(20);
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}
