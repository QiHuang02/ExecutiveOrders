package net.atired.executiveorders.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class PalePileItem extends Item{
    public PalePileItem(Settings settings) {
        super(settings);
    }
    public void addItems(ItemStack stack, List<ItemStack> stacks){
        stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stacks));
    }
    public void dummy(){

    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return super.allowComponentsUpdateAnimation(player, hand, oldStack, newStack) && !(oldStack.getDamage()+1==newStack.getDamage());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = (user.getStackInHand(hand));
        if((user.getStackInHand(hand)).get(DataComponentTypes.CONTAINER)!=null){
            for(ItemStack stack1 : ((ContainerComponent)stack.get(DataComponentTypes.CONTAINER)).streamNonEmpty().toList()){
                user.dropItem(stack1,true);
            }
            stack.setCount(stack.getCount()-1);
            return TypedActionResult.success(stack,true);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(world instanceof ServerWorld world1)
        {
            stack.damage(1,world1,null,this::dummy);
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void dummy(Item item) {

    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if(clickType == ClickType.RIGHT){
            if(stack.get(DataComponentTypes.CONTAINER)!=null){
                for(ItemStack stack1 : ((ContainerComponent)stack.get(DataComponentTypes.CONTAINER)).streamNonEmpty().toList()){
                    player.giveItemStack(stack1);
                }
                stack.setCount(stack.getCount()-1);
                return true;
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }
    @Override
    public int getItemBarStep(ItemStack stack) {
        return super.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb(0.26f,0.2f,0.5f);
    }
}
