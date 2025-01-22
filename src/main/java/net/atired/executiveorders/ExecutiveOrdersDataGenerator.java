package net.atired.executiveorders;

import net.atired.executiveorders.data.generator.ExecutiveOrdersEnchantmentGenerator;
import net.atired.executiveorders.data.generator.ExecutiveOrdersWorldGeneration;
import net.atired.executiveorders.init.worldgen.ConfiguredFeatureInit;
import net.atired.executiveorders.init.worldgen.PlacedFeatureInit;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class ExecutiveOrdersDataGenerator  implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ExecutiveOrdersEnchantmentGenerator::new);
        pack.addProvider(ExecutiveOrdersWorldGeneration::new);
    }
    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeatureInit::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PlacedFeatureInit::bootstrap);
    }
}
