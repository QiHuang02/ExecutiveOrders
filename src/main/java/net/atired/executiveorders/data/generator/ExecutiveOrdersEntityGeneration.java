package net.atired.executiveorders.data.generator;

import net.atired.executiveorders.enemies.custom.JauntEntity;
import net.atired.executiveorders.init.EOEntityTypeInit;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.Heightmap;

public class ExecutiveOrdersEntityGeneration {
    public static void addSpawns(){
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE), SpawnGroup.MONSTER, EOEntityTypeInit.VITRIFIED,20,2,3);
        SpawnRestriction.register(EOEntityTypeInit.VITRIFIED, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.END_CITY_HAS_STRUCTURE), SpawnGroup.MONSTER, EOEntityTypeInit.JAUNT,1,1,1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_NETHER), SpawnGroup.MONSTER, EOEntityTypeInit.JAUNT,50,7,9);
        SpawnRestriction.register(EOEntityTypeInit.JAUNT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, JauntEntity::canJauntSpawn);
    }
}
