package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class EOItemGroups {
    public static final ItemGroup ALL_EO_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            ExecutiveOrders.id("executive_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(EOItemsInit.PALE_PILE))
                    .displayName(Text.translatable("itemgroup.executiveorders.executive_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(EOItemsInit.PALE_PILE);
                        entries.add(EOItemsInit.HAUNTED_AXE);

                        entries.add(EOItemsInit.WARPEDEFFIGY);
                        entries.add(EOItemsInit.DIGGERSHARK);
                        entries.add(EOItemsInit.RIPPERSHARK);
                        entries.add(EOItemsInit.SWORDFISH);

                        entries.add(EOBlocksInit.VITRIC_CAMPFIRE);
                        entries.add(EOBlocksInit.MONOLITH);

                        entries.add(EOBlocksInit.BEDROCK_LEAVES);

                    }).build());
    public static void init(){

    }
}
