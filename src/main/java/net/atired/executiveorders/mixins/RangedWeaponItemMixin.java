package net.atired.executiveorders.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.atired.executiveorders.accessors.PersistentProjectileEntityAccessor;
import net.atired.executiveorders.init.EnchantmentEffectComponentTypesInit;
import net.atired.executiveorders.networking.payloads.ArbalestPayload;
import net.atired.executiveorders.networking.payloads.ExecutePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {
    @ModifyReturnValue(method = "createArrowEntity", at = @At("RETURN"))
    private ProjectileEntity enchancement$allowLoadingProjectile(ProjectileEntity original, World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        if(EnchantmentHelper.hasAnyEnchantmentsWith(weaponStack, EnchantmentEffectComponentTypesInit.ARBALEST) && original instanceof PersistentProjectileEntityAccessor accessor)
        {
            if(critical)
                accessor.setArbalest(2);
            return original;
        }
        return original;
    }
}