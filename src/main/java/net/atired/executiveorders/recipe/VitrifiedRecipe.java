package net.atired.executiveorders.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.function.Function;

public class VitrifiedRecipe implements Recipe<SingleStackRecipeInput> {
    protected final ItemStack output;
    protected final Ingredient recipeItem;
    protected final int cookingTime;
    public VitrifiedRecipe(Ingredient ingredients, ItemStack stack, int cookingTime)
    {
        this.output = stack;
        this.recipeItem = ingredients;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {

        if(world.isClient())
            return false;
        return recipeItem.test(input.item());
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ExecutiveOrdersRecipes.VITRIFYING;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    public static class Type implements RecipeType<VitrifiedRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "vitrified";
    }
    public interface RecipeFactory<T extends VitrifiedRecipe> {
        T create(Ingredient ingredient, ItemStack result,int cookingTime);


    }
    public static class Serializer<T extends VitrifiedRecipe> implements RecipeSerializer<T>{
        final RecipeFactory<T> recipeFactory;
        private final MapCodec<T> codec;
        private final PacketCodec<RegistryByteBuf, T> packetCodec;
        protected Serializer(RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.mapCodec((instance) -> {
                Products.P3 var10000 = instance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                    return recipe.recipeItem;
                }), ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter((recipe) -> {
                    return recipe.output;
                }), Codecs.NONNEGATIVE_INT.fieldOf("cookingtime").forGetter((recipe) -> {
                    return recipe.cookingTime;
                }));
                Objects.requireNonNull(recipeFactory);
                return var10000.apply(instance, (ingredient, result,cookingTime) -> {
                    return recipeFactory.create((Ingredient) ingredient, (ItemStack) result,(int)cookingTime);
                });
            });
            PacketCodec var10003 = Ingredient.PACKET_CODEC;
            Function var10004 = (recipe) -> {
                if(recipe instanceof VitrifiedRecipe vitrifiedRecipeRecipe)
                    return vitrifiedRecipeRecipe.recipeItem;
                else
                    return null;
            };
            PacketCodec var10005 = ItemStack.PACKET_CODEC;
            Function var10006 = (recipe) -> {
                if(recipe instanceof VitrifiedRecipe voidtouchedRecipe)
                    return voidtouchedRecipe.output;
                else
                    return null;
            };
            PacketCodec var100077 = PacketCodecs.INTEGER;
            Function var10007 = (recipe) -> {
                if(recipe instanceof VitrifiedRecipe voidtouchedRecipe)
                    return voidtouchedRecipe.cookingTime;
                else
                    return null;
            };
            Objects.requireNonNull(recipeFactory);
            this.packetCodec = PacketCodec.tuple(var10003, var10004, var10005, var10006,var100077,var10007, recipeFactory::create);

        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public PacketCodec<RegistryByteBuf, T> packetCodec() {
            return this.packetCodec;
        }
    }
}
