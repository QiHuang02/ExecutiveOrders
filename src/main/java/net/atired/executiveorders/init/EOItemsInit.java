package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.items.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class EOItemsInit {
    public static final FoodComponent FISHOVEL = new FoodComponent.Builder().nutrition(3).saturationModifier(0.6f).snack().build();

    public static final Item WARPEDEFFIGY = registerItem("warpedeffigy", new WarpedEffigyItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item RIPPERSHARK = registerItem("ripper_shark", new FishAxeItem(ToolMaterials.STONE,new Item.Settings().food(FoodComponents.COOKED_COD)));
    public static final Item DIGGERSHARK = registerItem("digger_shark", new FiShovelItem(ToolMaterials.STONE,new Item.Settings().food(FISHOVEL)));
    public static final Item HAUNTED_AXE = registerItem("haunted_axe", new HauntedAxeItem(new Item.Settings().maxCount(1).component(EODataComponentTypeInit.AXEHEAT,0).attributeModifiers(HauntedAxeItem.createAttributeModifiers(ToolMaterials.IRON,6,-3))));
    public static final Item SWORDFISH = registerItem("sword_fish", new FishSwordItem(ToolMaterials.WOOD,new Item.Settings().food(FoodComponents.COOKED_COD).attributeModifiers(FishSwordItem.createAttributeModifiers(ToolMaterials.DIAMOND, 2, -3f))));
    public static final Item PALE_PILE = registerItem("pale_pile", new PalePileItem(new Item.Settings().maxCount(1).maxDamage(168).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, ExecutiveOrders.id(name),item);
    }
    public static void register(){

    }
}
