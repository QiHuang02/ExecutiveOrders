package net.atired.executiveorders.tags;

import com.sun.jna.platform.win32.WinUser;
import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class EOEnchantmentTags {
    public static final TagKey<Enchantment> HOLLOW_CORE_TAG = TagKey.of(RegistryKeys.ENCHANTMENT, ExecutiveOrders.id("hollow_core"));
    public static final TagKey<Item> VOIDTOUCHED_TAG = TagKey.of(RegistryKeys.ITEM, ExecutiveOrders.id("voidtouched"));
    public static final TagKey<Item> VITRIFIED_TAG = TagKey.of(RegistryKeys.ITEM, ExecutiveOrders.id("vitrified"));
    public static final TagKey<Enchantment> ARBALEST_CONFLICT_TAG = TagKey.of(RegistryKeys.ENCHANTMENT, ExecutiveOrders.id("damage"));
    public static final TagKey<Block> FISHAXE_MINEABLE_TAG = TagKey.of(RegistryKeys.BLOCK, ExecutiveOrders.id("fishaxe_minable"));
    public static final TagKey<Block> FISHOVEL_MINEABLE_TAG = TagKey.of(RegistryKeys.BLOCK, ExecutiveOrders.id("fishovel_minable"));
}
