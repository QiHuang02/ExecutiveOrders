package net.atired.executiveorders.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atired.executiveorders.enemies.blockentity.BrushableBedrockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SuspiciousBedrockBlock extends BlockWithEntity {
    public static final MapCodec<SuspiciousBedrockBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Registries.BLOCK.getCodec().fieldOf("turns_into").forGetter(SuspiciousBedrockBlock::getBaseBlock),
                            Registries.SOUND_EVENT.getCodec().fieldOf("brush_sound").forGetter(SuspiciousBedrockBlock::getBrushingSound),
                            Registries.SOUND_EVENT.getCodec().fieldOf("brush_comleted_sound").forGetter(SuspiciousBedrockBlock::getBrushingCompleteSound),
                            createSettingsCodec()
                    )
                    .apply(instance, SuspiciousBedrockBlock::new)
    );
    private static final IntProperty DUSTED = Properties.DUSTED;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public static final int field_42773 = 2;
    private final Block baseBlock;
    private final SoundEvent brushingSound;
    private final SoundEvent brushingCompleteSound;

    @Override
    public MapCodec<SuspiciousBedrockBlock> getCodec() {
        return CODEC;
    }

    public SuspiciousBedrockBlock(Block baseBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, AbstractBlock.Settings settings) {
        super(settings);
        this.baseBlock = baseBlock;
        this.brushingSound = brushingSound;
        this.brushingCompleteSound = brushingCompleteSound;
        this.setDefaultState(this.stateManager.getDefaultState().with(DUSTED, Integer.valueOf(0)).with(LIT,false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DUSTED,LIT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 2);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleBlockTick(pos, this, 2);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof BrushableBedrockEntity brushableBlockEntity) {
            brushableBlockEntity.scheduledTick();
        }

    }



    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(DUSTED)>1) {


                double d = (double)pos.getX()+0.25 + random.nextDouble()/2;
                double e = (double)pos.getY() + 1.01;
                double f = (double)pos.getZ()+0.25 + random.nextDouble()/2;
                for (int i = 0; i < state.get(DUSTED); i++) {
                    world.addParticle(ParticleTypes.PORTAL, d, e, f, 0.0, 0.2, 0.0);
                    world.addParticle(ParticleTypes.REVERSE_PORTAL, d, e, f, 0.0, 0.4, 0.0);
                }


        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrushableBedrockEntity(pos, state);
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public SoundEvent getBrushingSound() {
        return this.brushingSound;
    }

    public SoundEvent getBrushingCompleteSound() {
        return this.brushingCompleteSound;
    }

}
