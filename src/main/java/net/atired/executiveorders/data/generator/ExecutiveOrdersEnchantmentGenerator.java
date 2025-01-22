package net.atired.executiveorders.data.generator;

import net.atired.executiveorders.enchanments.effects.ArbalestEnchantmentEffect;
import net.atired.executiveorders.enchanments.effects.ExecutionEnchantmentEffect;
import net.atired.executiveorders.enchanments.effects.PreciseImpactEnchantmentEffect;
import net.atired.executiveorders.init.EnchantmentEffectComponentTypesInit;
import net.atired.executiveorders.init.EnchantmentInit;
import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ExecutiveOrdersEnchantmentGenerator extends FabricDynamicRegistryProvider {
    public ExecutiveOrdersEnchantmentGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        RegistryWrapper<Item> itemLookup = registries.getWrapperOrThrow(RegistryKeys.ITEM);
        RegistryWrapper<Enchantment> enchantLookup = registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        register(entries, EnchantmentInit.EXECUTION_ENCHANT_KEY, Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.AXES),
                                6, // probability of showing up in the enchantment table
                                1, // max level
                                Enchantment.constantCost(16), // cost per level (base)
                                Enchantment.constantCost(20), // cost per level (max)
                                4, // anvil applying cost
                                AttributeModifierSlot.HAND
                        )).exclusiveSet(enchantLookup.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
                .addEffect(EnchantmentEffectComponentTypes.POST_ATTACK,
                        EnchantmentEffectTarget.ATTACKER,
                        EnchantmentEffectTarget.VICTIM,
                        new ExecutionEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.5f, 0.15f))));

        register(entries, EnchantmentInit.PRECISE_ENCHANT_KEY, Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                                6,
                                1,
                                Enchantment.constantCost(16),
                                Enchantment.constantCost(20),
                                4,
                                AttributeModifierSlot.HAND
                        )).exclusiveSet(enchantLookup.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))

                .addEffect(EnchantmentEffectComponentTypes.POST_ATTACK,
                        EnchantmentEffectTarget.ATTACKER,
                        EnchantmentEffectTarget.VICTIM,
                        new PreciseImpactEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.5f, 0.15f))));
        register(entries, EnchantmentInit.ARBALEST_ENCHANT_KEY, Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                1,
                                1,
                                Enchantment.constantCost(16),
                                Enchantment.constantCost(20),
                                2,
                                AttributeModifierSlot.HAND
                        )).exclusiveSet(enchantLookup.getOrThrow(EOEnchantmentTags.ARBALEST_CONFLICT_TAG))

                .addNonListEffect(EnchantmentEffectComponentTypesInit.ARBALEST,
                        new ArbalestEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.5f, 0.15f))));
    }

    private static void register(Entries entries, RegistryKey<Enchantment> key, Enchantment.Builder builder, ResourceCondition... resourceConditions) {
        entries.add(key, builder.build(key.getValue()), resourceConditions);
    }

    @Override
    public String getName() {
        return "Enchantment Generator";
    }
}
