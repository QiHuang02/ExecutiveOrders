package net.atired.executiveorders.items;

import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FishAxeItem extends MiningToolItem {
    public FishAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial,EOEnchantmentTags.FISHAXE_MINEABLE_TAG, settings);
    }

}
