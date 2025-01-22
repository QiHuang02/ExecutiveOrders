package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enemies.custom.JauntEntity;
import net.atired.executiveorders.enemies.custom.VitrifiedEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityTypeInit {

    public static final EntityType<VitrifiedEntity> VITRIFIED = EntityType.Builder.create(VitrifiedEntity::new, SpawnGroup.MONSTER).dimensions(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight()).build();
    public static final EntityType<JauntEntity> JAUNT = EntityType.Builder.create(JauntEntity::new, SpawnGroup.MONSTER).dimensions(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight()).build();
    public static void init(){
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("vitrified"), VITRIFIED);
        Registry.register(Registries.ENTITY_TYPE, ExecutiveOrders.id("jaunt"), JAUNT);
        FabricDefaultAttributeRegistry.register(VITRIFIED, VitrifiedEntity.createVitrifiedAttributes());
        FabricDefaultAttributeRegistry.register(JAUNT, JauntEntity.createJauntAttributes());
    }
}
