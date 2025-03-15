package net.atired.executiveorders;

import net.atired.executiveorders.data.generator.ExecutiveOrdersEntityGeneration;
import net.atired.executiveorders.init.*;
import net.atired.executiveorders.init.worldgen.BiomeModifInit;
import net.atired.executiveorders.init.worldgen.FeatureInit;
import net.atired.executiveorders.items.WarpedEffigyItem;
import net.atired.executiveorders.recipe.ExecutiveOrdersRecipes;
import net.atired.executiveorders.recipe.VitrifiedRecipe;
import net.atired.executiveorders.recipe.VoidtouchedRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Optional;
import java.util.Set;

public class ExecutiveOrders implements ModInitializer {
    public static final String MODID = "executiveorders";
    public static float YEAH = 0f;

    @Override
    public void onInitialize() {

        ItemsInit.register();
        BlocksInit.register();
        BlockEntityInit.initialize();
        ExecutiveOrdersRecipes.registerRecipes();
        EnchantmentEffectComponentTypesInit.init();
        MobEffectsInit.init();
        EntityTypeInit.init();
        EODataComponentTypeInit.init();
        ExecutiveOrdersEntityGeneration.addSpawns();
        EnchantmentInit.load();
        FeatureInit.init();
        BiomeModifInit.load();
        ParticlesInit.registerParticles();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(itemGroup -> {
            itemGroup.add(ItemsInit.DIGGERSHARK);
            itemGroup.add(ItemsInit.RIPPERSHARK);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup -> {
            itemGroup.add(ItemsInit.PALE_PILE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(itemGroup -> {
            itemGroup.add(BlocksInit.MONOLITH);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(itemGroup -> {
            itemGroup.add(BlocksInit.BEDROCK_LEAVES);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(itemGroup -> {
            itemGroup.add(BlocksInit.MONOLITH);
            itemGroup.add(BlocksInit.VITRIC_CAMPFIRE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(itemGroup -> {
            itemGroup.add(ItemsInit.SWORDFISH);
            itemGroup.add(ItemsInit.WARPEDEFFIGY);
            itemGroup.add(ItemsInit.HAUNTED_AXE);
        });
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity,damageSource,damage)->{
            for (ItemStack stack : entity.getHandItems()) {
                if(stack.getItem() instanceof WarpedEffigyItem){
                    stack.setCount(stack.getCount()-1);
                    entity.setHealth(1);
                    if(entity instanceof ServerPlayerEntity user){
                        Vec3d vec3d2 = Vec3d.of(user.getWorld().getSpawnPos());
                        if(user.getSpawnPointPosition()!=null)
                        {
                            vec3d2 = Vec3d.of(user.getSpawnPointPosition());
                        }
                        ServerWorld world = user.server.getWorld(user.getSpawnPointDimension());

                        for(LivingEntity entity1 : user.getServerWorld().getEntitiesByClass(LivingEntity.class,user.getBoundingBox().expand(3),LivingEntity::isAlive)){
                            entity1.teleport(world,vec3d2.x,vec3d2.y,vec3d2.z, Set.of(),entity1.getYaw(),entity1.getPitch());
                            entity1.teleport(entity1.getParticleX(15),entity1.getRandomBodyY(),entity1.getParticleZ(15),true);
                        }
                        user.getServerWorld().spawnParticles(ParticlesInit.EFFIGY_PARTICLE,user.getX(),user.getBodyY(0.5),user.getZ(),1,0,0,0,0);
                        user.teleport(world,vec3d2.x,vec3d2.y,vec3d2.z,user.getYaw(),user.getPitch());
                        user.teleport(user.getParticleX(10),user.getRandomBodyY(),user.getParticleZ(10),true);
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,  30, 4));
                        spawn2Particles(user.getServerWorld(),ParticlesInit.EFFIGY_PARTICLE,user.getX(),user.getBodyY(0.5),user.getZ(),1,0,0,0,0);
                    }
                    return false;
                }
            }
            return true;
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((unloaded,world)->{
            if(unloaded.getRemovalReason() == Entity.RemovalReason.DISCARDED)
            {

                if(unloaded instanceof ItemEntity itemEntity && unloaded.getY() < -20 && world.getDimensionEntry().getKey().get() == DimensionTypes.THE_END)
                {
                    ItemStack stack = itemEntity.getStack();
                    Optional<RecipeEntry<VoidtouchedRecipe>> recipe = world.getRecipeManager().getFirstMatch(VoidtouchedRecipe.Type.INSTANCE, new SingleStackRecipeInput(stack), world);

                    if(itemEntity.getPos().length()>9100)
                    {
                        FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
                        if(foodComponent!=null && stack.get(EODataComponentTypeInit.VITRIC) == null){
                            FoodComponent foodComponentNew = new FoodComponent((int) Math.ceil(foodComponent.nutrition()/1.5),foodComponent.saturation()/1.5f,foodComponent.canAlwaysEat(),foodComponent.eatSeconds()/2,foodComponent.usingConvertsTo(),foodComponent.effects());
                            stack.set(DataComponentTypes.FOOD,foodComponentNew);
                            stack.set(EODataComponentTypeInit.VITRIC,1);
                        }
                        Optional<RecipeEntry<VitrifiedRecipe>> recipeVitric = world.getRecipeManager().getFirstMatch(VitrifiedRecipe.Type.INSTANCE, new SingleStackRecipeInput(stack), world);
                        Optional<RecipeEntry<SmeltingRecipe>> recipe2 = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(stack), world);
                        if(recipeVitric.isPresent()){
                            stack = stack.withItem(recipeVitric.get().value().getResult(null).getItem());
                        }
                        else if(recipe2.isPresent())
                        {
                            stack = stack.withItem(recipe2.get().value().getResult(null).getItem());
                        }
                    } else if(recipe.isPresent())
                    {
                        stack = stack.withItem(recipe.get().value().getResult(null).getItem());
                    }

                    ItemEntity newItemEntity = new ItemEntity(world,itemEntity.getX(),itemEntity.getY()+20,itemEntity.getZ(),stack);

                    newItemEntity.setNoGravity(true);
                    newItemEntity.addVelocity(0,1.5,0);
                    world.spawnEntity(newItemEntity);
                }
            }
        });
    }
    public <T extends ParticleEffect> int spawn2Particles(ServerWorld world, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, true, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int i = 0;
        for (int j = 0; j < world.getPlayers().size(); ++j) {
            ServerPlayerEntity serverPlayerEntity = world.getPlayers().get(j);
            if (!world.sendToPlayerIfNearby(serverPlayerEntity, true, x, y, z, particleS2CPacket)) continue;
            ++i;
        }
        return i;
    }
    public static Identifier id(String path){
        return Identifier.of(MODID,path);
    }
}
