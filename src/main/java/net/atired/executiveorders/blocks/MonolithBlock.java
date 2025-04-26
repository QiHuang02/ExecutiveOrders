package net.atired.executiveorders.blocks;

import com.mojang.serialization.MapCodec;
import net.atired.executiveorders.enemies.blockentity.MonolithBlockEntity;
import net.atired.executiveorders.init.EOBlockEntityInit;
import net.atired.executiveorders.networking.payloads.MonolithPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MonolithBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final MapCodec<MonolithBlock> CODEC = MonolithBlock.createCodec(MonolithBlock::new);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    public MonolithBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = false;

        boolean bl2 = world.isReceivingRedstonePower(pos);
        if (!this.getDefaultState().isOf(sourceBlock)&&bl2 !=isPowered(state)) {
            MonolithBlockEntity monolithBlock = ((MonolithBlockEntity) world.getBlockEntity(pos));
            monolithBlock.alphaticks = 20;
            if(world instanceof ServerWorld serverWorld&&bl2)    {
                for (ServerPlayerEntity gamer : PlayerLookup.tracking(monolithBlock)) {
                    ServerPlayNetworking.send(gamer, new MonolithPayload(pos.getX(),pos.getY(),pos.getZ(),20));
                }
            }


            world.setBlockState(pos, (BlockState) state.with(POWERED, bl2), Block.NOTIFY_LISTENERS);
        }
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MonolithBlockEntity(pos, state);
    }
    @Override
    protected int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
    public boolean isPowered(BlockState state){
        return state.get(POWERED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return MonolithBlock.validateTicker(world, type, EOBlockEntityInit.MONOLITH_ENTITY_TYPE);
    }
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> validateTicker(World world, BlockEntityType<T> givenType, BlockEntityType<? extends MonolithBlockEntity> expectedType) {
        return world.isClient ? null : MonolithBlock.validateTicker(givenType, expectedType, MonolithBlockEntity::tick);
    }
}
