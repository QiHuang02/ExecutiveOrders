package net.atired.executiveorders.blocks;

import net.atired.executiveorders.enemies.blockentity.VitricCampfireBlockEntity;
import net.atired.executiveorders.init.BlockEntityInit;
import net.atired.executiveorders.init.ParticlesInit;
import net.atired.executiveorders.recipe.VitrifiedRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VitricCampfireBlock extends CampfireBlock {
    public VitricCampfireBlock(Settings settings) {
        super(true, 3, settings);
    }
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Boolean)state.get(LIT)) {
            if (random.nextInt(10) == 0) {
                world.playSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
                        SoundCategory.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F,
                        false
                );
            }



        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return state.get(LIT) ? validateTicker(type, BlockEntityInit.VITRIC_CAMPFIRE_ENTITY_TYPE, VitricCampfireBlockEntity::clientTick) : null;
        } else {
            return state.get(LIT)
                    ? validateTicker(type, BlockEntityInit.VITRIC_CAMPFIRE_ENTITY_TYPE, VitricCampfireBlockEntity::litServerTick)
                    : validateTicker(type, BlockEntityInit.VITRIC_CAMPFIRE_ENTITY_TYPE, VitricCampfireBlockEntity::unlitServerTick);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VitricCampfireBlockEntity(pos, state);
    }
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof VitricCampfireBlockEntity campfireBlockEntity) {
            ItemStack itemStack = player.getStackInHand(hand);
            Optional<RecipeEntry<VitrifiedRecipe>> optional = campfireBlockEntity.getRecipeFor(itemStack);
            if (optional.isPresent()) {
                if (!world.isClient && campfireBlockEntity.addItem(player, itemStack, ((VitrifiedRecipe)((RecipeEntry)optional.get()).value()).getCookingTime())) {
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ItemActionResult.SUCCESS;
                }

                return ItemActionResult.CONSUME;
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
        Random random = world.getRandom();

        SimpleParticleType simpleParticleType =ParticlesInit.SMALL_VOID_PARTICLE;
        world.addImportantParticle(
                simpleParticleType,
                true,
                (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
                (double)pos.getY() + random.nextDouble()+ 0.4,
                (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
                0.0,
                0.25,
                0.0
        );
    }
}
