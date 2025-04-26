package net.atired.executiveorders.init.worldgen;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.worldgen.features.BedrockPillarFeature;
import net.atired.executiveorders.worldgen.features.VoidPillarFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.*;

public class FeatureInit {
    public static final Feature<DefaultFeatureConfig> BEDROCK_PILLARS = register("bedrock_pillars", new BedrockPillarFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> VOID_PILLARS = register("void_pillars", new VoidPillarFeature(DefaultFeatureConfig.CODEC));
    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return (F) Registry.register(Registries.FEATURE, ExecutiveOrders.id(name), feature);
    }
    public static void init(){

    }
}
