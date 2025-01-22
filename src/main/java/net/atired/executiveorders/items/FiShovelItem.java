package net.atired.executiveorders.items;

import net.atired.executiveorders.tags.EOEnchantmentTags;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;

public class FiShovelItem extends MiningToolItem {
    public FiShovelItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial,EOEnchantmentTags.FISHOVEL_MINEABLE_TAG, settings);
    }
}
