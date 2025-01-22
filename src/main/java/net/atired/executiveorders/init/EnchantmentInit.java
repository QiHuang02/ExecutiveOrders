package net.atired.executiveorders.init;

import com.mojang.serialization.MapCodec;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enchanments.effects.ArbalestEnchantmentEffect;
import net.atired.executiveorders.enchanments.effects.ExecutionEnchantmentEffect;
import net.atired.executiveorders.enchanments.effects.PreciseImpactEnchantmentEffect;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class EnchantmentInit {
    public static final RegistryKey<Enchantment> EXECUTION_ENCHANT_KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, ExecutiveOrders.id("execution"));
    public static final MapCodec<ExecutionEnchantmentEffect> EXECUTION_EFFECT = register("execution",ExecutionEnchantmentEffect.CODEC);
    public static final RegistryKey<Enchantment> PRECISE_ENCHANT_KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, ExecutiveOrders.id("precisestrike"));
    public static final MapCodec<PreciseImpactEnchantmentEffect> PRECISE_STRIKE_EFFECT = register("precisestrike",PreciseImpactEnchantmentEffect.CODEC);
    public static final RegistryKey<Enchantment> ARBALEST_ENCHANT_KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, ExecutiveOrders.id("arbalest"));
    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String name, MapCodec<T> codec)
    {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE,ExecutiveOrders.id(name),codec);
    }
    public static void load(){

    }
}
