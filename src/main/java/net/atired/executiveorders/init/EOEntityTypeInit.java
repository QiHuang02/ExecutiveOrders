package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enemies.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EOEntityTypeInit {

    public static final EntityType<VitrifiedEntity> VITRIFIED = EntityType.Builder.create(VitrifiedEntity::new, SpawnGroup.MONSTER).dimensions(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight()).build();
    public static final EntityType<JauntEntity> JAUNT = EntityType.Builder.create(JauntEntity::new, SpawnGroup.MONSTER).dimensions(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).build();
    public static final EntityType<IcoSphereEntity> ICOSPHERE = EntityType.Builder.create(IcoSphereEntity::new, SpawnGroup.MISC).dimensions(3, 3).build();

    public static final EntityType<StarFallEntity> STARFALL = EntityType.Builder.create(StarFallEntity::new, SpawnGroup.MISC).dimensions(1, 1).build();
    public static final EntityType<DeathRayEntity> DEATHRAY = EntityType.Builder.<DeathRayEntity>create(DeathRayEntity::new, SpawnGroup.MISC).dimensions(1, 1).disableSaving().build();

    public static void init(){
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("vitrified"), VITRIFIED);
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("jaunt"), JAUNT);
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("icosphere"), ICOSPHERE);
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("starfall"), STARFALL);
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("deathray"), DEATHRAY);
        FabricDefaultAttributeRegistry.register(VITRIFIED, VitrifiedEntity.createVitrifiedAttributes());
        FabricDefaultAttributeRegistry.register(JAUNT, JauntEntity.createJauntAttributes());
    }
}
