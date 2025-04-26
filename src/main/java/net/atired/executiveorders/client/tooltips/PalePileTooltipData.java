package net.atired.executiveorders.client.tooltips;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public class PalePileTooltipData implements TooltipData {
    private List<ItemStack> stacks = List.of();
    public PalePileTooltipData(List<ItemStack> stackList){
        stacks = stackList;
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }
}
