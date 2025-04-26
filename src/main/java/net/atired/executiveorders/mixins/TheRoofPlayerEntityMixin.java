package net.atired.executiveorders.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class TheRoofPlayerEntityMixin extends LivingEntity {
    protected TheRoofPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick",at=@At("HEAD"))
    private void tickSpeeded(CallbackInfo ci){
        if(this.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER &&this.getPos().y>123){
            this.setVelocity(this.getVelocity().multiply(1.01));
        }
    }
}
