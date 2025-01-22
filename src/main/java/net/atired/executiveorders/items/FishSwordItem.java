package net.atired.executiveorders.items;

import net.atired.executiveorders.init.ParticlesInit;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FishSwordItem extends SwordItem {
    public static final Identifier BASE_RANGE_MODIFIER_ID = Identifier.ofVanilla("base_entity_interaction_range");
    public FishSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, int baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, (float)baseAttackDamage + material.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,new EntityAttributeModifier(BASE_RANGE_MODIFIER_ID, 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).build();
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world= target.getWorld();
    for(LivingEntity a : world.getEntitiesByClass(LivingEntity.class,target.getBoundingBox().expand(2,0,2),LivingEntity::isLiving))
        {
            if(a!=attacker && world instanceof ServerWorld world1)
            {
                world1.spawnParticles(ParticlesInit.SPLISH_PARTICLE,a.getX(),a.getBodyY(0f),a.getZ(),1,0,0,0,0);
                world1.spawnParticles(ParticleTypes.SPLASH,a.getX(),a.getBodyY(0.5f),a.getZ(),4,0.4,0.8,0.4,0.01);
                a.addVelocity(0,0.9,0);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        super.postDamageEntity(stack, target, attacker);
    }
}
