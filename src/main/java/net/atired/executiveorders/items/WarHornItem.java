package net.atired.executiveorders.items;

import net.atired.executiveorders.networking.payloads.WarHornPayload;
import net.atired.executiveorders.particles.custom.types.BounceParticleEffect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WarHornItem extends GoatHornItem {
    public WarHornItem(Settings settings, TagKey<Instrument> instrumentTag) {
        super(settings, instrumentTag);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);


        MutableText mutableText = Text.literal("?");
        MutableText text = tooltip.getLast().copy();
        tooltip.removeLast();
        tooltip.add(text.append(mutableText));

    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        ItemStack itemStack = user.getStackInHand(hand);

            Vec3d vec = user.getVelocity();
            user.setVelocity(vec.getX()/8,0.5,vec.getZ()/8);
            Vec3d rot = user.getRotationVec(0);
            world.addParticle(new BounceParticleEffect(rot.toVector3f(),0f),user.getX()+rot.getX(),user.getEyeY()+rot.getY(),user.getZ()+rot.getZ(),rot.getX(),rot.getY(),rot.getZ());
            world.addParticle(new BounceParticleEffect(rot.toVector3f(),0.8f),user.getX()+rot.getX()*2,user.getEyeY()+rot.getY()*2,user.getZ()+rot.getZ()*2,rot.getX()*1.5,rot.getY()*1.5,rot.getZ()*1.5);
            for(LivingEntity target : world.getEntitiesByClass(LivingEntity.class,user.getBoundingBox().expand(7),LivingEntity::isAlive)){
                if(target!=user){
                    double dist = target.getPos().distanceTo(user.getPos());
                    Vec3d dir = target.getPos().subtract(user.getPos()).normalize().add(0,1.2,0).multiply(1.2);
                    if(world instanceof ServerWorld world1 && target instanceof ServerPlayerEntity player){
                        ServerPlayNetworking.send(player,new WarHornPayload(dir.toVector3f(), (float) (Math.clamp(dist+6f,0.0f,10.0f)/10f)));
                    }

                    if(dist<7){
                        target.setVelocity(dir);
                    }
                }
            }

        return result;
    }
}
