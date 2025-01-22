package net.atired.executiveorders.misc;

import net.atired.executiveorders.init.ItemsInit;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class HauntedAxeMaterial implements ToolMaterial {
    public static final HauntedAxeMaterial INSTANCE = new HauntedAxeMaterial();

    @Override
    public int getDurability() {
        return 600;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 3.5F;
    }

    @Override
    public float getAttackDamage() {
        return 3.0f;
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return BlockTags.INCORRECT_FOR_IRON_TOOL;
    }

    @Override
    public int getEnchantability() {
        return 17;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.IRON_INGOT, ItemsInit.PALE_PILE);
    }
}
