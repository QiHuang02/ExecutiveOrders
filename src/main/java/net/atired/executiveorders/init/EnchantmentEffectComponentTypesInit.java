package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enchanments.effects.ArbalestEnchantmentEffect;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class EnchantmentEffectComponentTypesInit {
    public static final ComponentType<ArbalestEnchantmentEffect> ARBALEST = register("arbalest", builder -> builder.codec(ArbalestEnchantmentEffect.CODEC));
    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ExecutiveOrders.id(id), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
    }

}

