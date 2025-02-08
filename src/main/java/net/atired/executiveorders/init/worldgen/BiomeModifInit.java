package net.atired.executiveorders.init.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class BiomeModifInit {
    public static void load() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_DECORATION,
                PlacedFeatureInit.BEDROCK_PILLARS_KEY
        );
    }
}
