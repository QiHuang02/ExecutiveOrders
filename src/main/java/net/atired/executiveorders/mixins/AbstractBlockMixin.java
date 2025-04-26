package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.init.EOEntityTypeInit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockMixin {
    @Shadow public abstract Block getBlock();

    @ModifyReturnValue(method = "allowsSpawning(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z",at = @At("RETURN"))
    private boolean shouldAllowin(boolean original, BlockView world, BlockPos pos, EntityType<?> type){
        if(type == EOEntityTypeInit.JAUNT && getBlock() == Blocks.BEDROCK)
        {
            return true;
        }
        return original;
    }
}
