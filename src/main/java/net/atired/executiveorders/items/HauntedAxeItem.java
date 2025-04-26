package net.atired.executiveorders.items;

import net.atired.executiveorders.init.EODataComponentTypeInit;
import net.atired.executiveorders.misc.HauntedAxeMaterial;
import net.atired.executiveorders.networking.payloads.HauntedAxePayload;
import net.atired.executiveorders.particles.custom.types.SlashParticleEffect;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HauntedAxeItem extends AxeItem {
    public HauntedAxeItem(Settings settings) {
        super(HauntedAxeMaterial.INSTANCE, settings);
    }
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean postMine = super.postMine(stack, world, state, pos, miner);
        if(postMine && stack.get(EODataComponentTypeInit.AXEHEAT)!=null){
            stack.set(EODataComponentTypeInit.AXEHEAT,Math.clamp(stack.get(EODataComponentTypeInit.AXEHEAT)+20,0,200));
        }
        return postMine;
    }
    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(stack.get(EODataComponentTypeInit.AXEHEAT)!=null&&stack.get(EODataComponentTypeInit.AXEHEAT)>0)
            stack.set(EODataComponentTypeInit.AXEHEAT,stack.get(EODataComponentTypeInit.AXEHEAT)-1);

        super.inventoryTick(stack, world, entity, slot, selected);
    }


    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        if(damageSource.getWeaponStack().get(EODataComponentTypeInit.AXEHEAT)!=null){
            return super.getBonusAttackDamage(target, baseAttackDamage, damageSource) + damageSource.getWeaponStack().get(EODataComponentTypeInit.AXEHEAT)/70f;
        }
        return super.getBonusAttackDamage(target, baseAttackDamage, damageSource);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(attacker instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld world){
            int heater =stack.get(EODataComponentTypeInit.AXEHEAT);
            int heat =Math.clamp(stack.get(EODataComponentTypeInit.AXEHEAT)+40,0,200);
            stack.set(EODataComponentTypeInit.AXEHEAT,heat);
            for(ServerPlayerEntity player1 : PlayerLookup.tracking(world,player.getBlockPos())){
                ServerPlayNetworking.send(player1,new HauntedAxePayload(heat,stack));
            }
            Vec3d rotdir = attacker.getRotationVec(0);
            target.setVelocity(target.getVelocity().multiply(0.2));
            world.spawnParticles(new SlashParticleEffect(rotdir.toVector3f(),heater/600f),player.getX(),player.getEyeY(),player.getZ(),1,0.1,0.1,0.1,0);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        if(stack.get(EODataComponentTypeInit.AXEHEAT)!=null){
            float axeheat = Math.clamp(stack.get(EODataComponentTypeInit.AXEHEAT),0,200)/200f;
            return MathHelper.hsvToRgb(0.62f+axeheat/5f,axeheat*0.5f,0.7f);
        }
        return super.getItemBarColor(stack);
    }
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float axeheat = Math.clamp(stack.get(EODataComponentTypeInit.AXEHEAT),0,200)/15f+1f;
        float returnal = super.getMiningSpeed(stack, state);
        if(returnal!=1.0f) returnal =  super.getMiningSpeed(stack, state)*axeheat;

        return returnal;
    }
}
