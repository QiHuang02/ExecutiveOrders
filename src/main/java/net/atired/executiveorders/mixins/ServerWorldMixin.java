package net.atired.executiveorders.mixins;

import net.atired.executiveorders.enemies.custom.StarFallEntity;
import net.atired.executiveorders.init.EOEntityTypeInit;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Shadow protected abstract BlockPos getLightningPos(BlockPos pos);

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",at=@At("HEAD"))
    private void starTick(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci){
        ChunkPos chunkPos = chunk.getPos();

        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();

        if (getDimensionEntry().getKey().get() == DimensionTypes.THE_END && new Vec3d(chunkPos.getCenterX(),0,chunkPos.getCenterZ()).length()>2000&& new Vec3d(chunkPos.getCenterX(),0,chunkPos.getCenterZ()).length()<3000&& this.random.nextInt(1000) == 0) {
            BlockPos blockPos = this.getLightningPos(this.getRandomPosInChunk(i, 0, j, 15));
                blockPos = blockPos.up(255-blockPos.getY());
                StarFallEntity starFallEntity = EOEntityTypeInit.STARFALL.create(this);
                if (starFallEntity != null) {
                    starFallEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));

                    this.spawnEntity(starFallEntity);
                }

        }
    }
}
