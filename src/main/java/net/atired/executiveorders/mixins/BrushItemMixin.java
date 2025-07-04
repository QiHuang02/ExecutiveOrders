package net.atired.executiveorders.mixins;

import net.atired.executiveorders.enemies.blockentity.BrushableBedrockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public abstract class BrushItemMixin {
    @Shadow protected abstract HitResult getHitResult(PlayerEntity user);

    @Inject(method = "usageTick",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"))
    private void susBedrockUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci){
        if (remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
            HitResult hitResult = this.getHitResult(playerEntity);


            if (hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                BlockState blockState = world.getBlockState(blockPos);
                if (!world.isClient() && world.getBlockEntity(blockPos) instanceof BrushableBedrockEntity brushableBlockEntity) {
                    boolean bl2 = brushableBlockEntity.brush(world.getTime(), playerEntity, blockHitResult.getSide());
                    if (bl2) {
                        EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                        stack.damage(1, user, equipmentSlot);
                    }
                }
            }
        }
    }
}
