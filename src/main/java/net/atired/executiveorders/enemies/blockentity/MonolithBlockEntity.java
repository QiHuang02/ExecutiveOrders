package net.atired.executiveorders.enemies.blockentity;

import net.atired.executiveorders.init.EOBlockEntityInit;
import net.atired.executiveorders.networking.payloads.MonolithPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MonolithBlockEntity extends BlockEntity {
    public int alphaticks = 0;
    public MonolithBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public MonolithBlockEntity(BlockPos pos, BlockState state) {
        super(EOBlockEntityInit.MONOLITH_ENTITY_TYPE, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, MonolithBlockEntity blockEntity) {
        if(blockEntity.alphaticks>0){
            blockEntity.alphaticks-=1;
            if(world instanceof ServerWorld serverWorld)    {
                for (ServerPlayerEntity gamer : PlayerLookup.tracking(blockEntity)) {
                    ServerPlayNetworking.send(gamer, new MonolithPayload(pos.getX(),pos.getY(),pos.getZ(), blockEntity.alphaticks));
                }
            }
        }
    }

}
