package net.atired.executiveorders.init.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class BiomeModifInit {
    public static void load() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.FLUID_SPRINGS,
                PlacedFeatureInit.BEDROCK_PILLARS_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.DEEP_DARK),
                GenerationStep.Feature.FLUID_SPRINGS,
                PlacedFeatureInit.VOID_PILLARS_KEY
        );
    }
}
