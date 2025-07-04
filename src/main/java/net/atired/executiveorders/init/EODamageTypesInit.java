package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class EODamageTypesInit {
    public static RegistryKey<DamageType> UNRAVELING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ExecutiveOrders.id("unraveled"));
    public static RegistryKey<DamageType> STARSTRUCK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ExecutiveOrders.id("starstruck"));

    public static void init(){

    }
}
