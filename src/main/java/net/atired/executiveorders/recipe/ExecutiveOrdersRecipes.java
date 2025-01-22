package net.atired.executiveorders.recipe;

import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.recipe.CraftingDecoratedPotRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ExecutiveOrdersRecipes<T extends Recipe<?>> {
    public static final RecipeSerializer<VoidtouchedRecipe> VOIDING = register(ExecutiveOrders.id("voidtouched"),new VoidtouchedRecipe.Serializer<VoidtouchedRecipe>(VoidtouchedRecipe::new));
    public static final RecipeSerializer<VitrifiedRecipe> VITRIFYING = register(ExecutiveOrders.id("vitrified"),new VitrifiedRecipe.Serializer<VitrifiedRecipe>(VitrifiedRecipe::new));
    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(Identifier id, S serializer) {
        return (S)Registry.register(Registries.RECIPE_SERIALIZER,  id, serializer);
    }
    public static void registerRecipes(){
        Registry.register(Registries.RECIPE_TYPE,ExecutiveOrders.id(VoidtouchedRecipe.Type.ID),VoidtouchedRecipe.Type.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE,ExecutiveOrders.id(VitrifiedRecipe.Type.ID),VitrifiedRecipe.Type.INSTANCE);
    }
}
