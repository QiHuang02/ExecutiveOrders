package net.atired.executiveorders.enchanments.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atired.executiveorders.accessors.LivingEntityAccessor;
import net.atired.executiveorders.networking.payloads.ExecutePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record ArbalestEnchantmentEffect(EnchantmentLevelBasedValue amount){
    public static final Codec<ArbalestEnchantmentEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(ArbalestEnchantmentEffect::amount)
    ).apply(instance, ArbalestEnchantmentEffect::new));
}
