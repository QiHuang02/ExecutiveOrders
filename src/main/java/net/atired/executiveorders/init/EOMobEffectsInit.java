package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.effects.PhasingEffect;
import net.atired.executiveorders.effects.ScrongBongledEffect;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class EOMobEffectsInit {
    public static final RegistryEntry<StatusEffect> PHASING_EFFECT;
    public static final RegistryEntry<StatusEffect> SCRONGBONGLED_EFFECT;

    static {
        SCRONGBONGLED_EFFECT = Registry.registerReference(Registries.STATUS_EFFECT, ExecutiveOrders.id("scrongbongled"), new ScrongBongledEffect());

        PHASING_EFFECT = Registry.registerReference(Registries.STATUS_EFFECT, ExecutiveOrders.id("phasing"), new PhasingEffect());
    }
    public static void init(){

    }
}