package net.atired.executiveorders.init;


import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.blocks.MonolithBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlocksInit {
    public static final Block MONOLITH = new MonolithBlock(Block.Settings.create().strength(4.0f).nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never));
    private static void registerBlockItems(String name, Block block){
        Registry.register(Registries.ITEM,ExecutiveOrders.id(name),new BlockItem(block,new Item.Settings().fireproof()));
    }
    public static void register(){
        Registry.register(Registries.BLOCK, ExecutiveOrders.id("monolith"), MONOLITH);
        registerBlockItems("monolith",MONOLITH);
    }
}
