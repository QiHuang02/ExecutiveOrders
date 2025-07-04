package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.UseAction;

public class EOPotionsInit {
    public static final RegistryEntry<Potion> PHASING_POTION =
            Registry.registerReference(Registries.POTION, ExecutiveOrders.id("phasing_normal"),
                    new Potion(new StatusEffectInstance(EOMobEffectsInit.PHASING_EFFECT, 78, 0)));
    public static final RegistryEntry<Potion> BONGLED_POTION =
            Registry.registerReference(Registries.POTION, ExecutiveOrders.id("scrongbongled_normal"),
                    new Potion(new StatusEffectInstance(EOMobEffectsInit.SCRONGBONGLED_EFFECT, 220, 0)));

    public static void registerPotions(){
    }

}
