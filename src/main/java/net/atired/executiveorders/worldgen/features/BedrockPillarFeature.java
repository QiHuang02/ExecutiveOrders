package net.atired.executiveorders.worldgen.features;

import com.mojang.serialization.Codec;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BedrockPillarFeature
        extends Feature<DefaultFeatureConfig> {
    public BedrockPillarFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        BlockPos.Mutable blockPos1 = blockPos.mutableCopy();
        float randumb = EOgetDatNoise.sampleNoise3D(blockPos.getX(),blockPos.getY(),blockPos.getZ(),250f);
        if(randumb>0.90f && (blockPos.getX()+blockPos.getZ())%2 == 0){
            for (int i = 0; i < 16*((randumb-0.9f)*10); i++){
                if( structureWorldAccess.isAir(blockPos1)){
                    i+=5;
                }
                structureWorldAccess.setBlockState(blockPos1, Blocks.BEDROCK.getDefaultState(), Block.NOTIFY_LISTENERS);
                blockPos1 = blockPos1.move(Direction.UP);
            }
            return true;
        }

        return false;
    }
}

