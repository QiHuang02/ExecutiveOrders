package net.atired.executiveorders.worldgen.features;

import com.mojang.serialization.Codec;
import net.atired.executiveorders.init.EOBlocksInit;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class VoidPillarFeature
        extends Feature<DefaultFeatureConfig> {
    public VoidPillarFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        BlockPos blockPosClone = blockPos.mutableCopy();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        float randomWill = EOgetDatNoise.sampleNoise3D(blockPosClone.getX(),blockPosClone.getY(),blockPosClone.getZ(),400f);
        if(randomWill<0.4f){
            return false;
        }
        Random random = context.getRandom();
        float angle = random.nextFloat()*3.14f*2;
        boolean returnwhat = false;
        Vec3d centPos = new Vec3d(0,0,0);
        for (int k = 0; k < 6; k++) {
            centPos = centPos.add(new Vec3d(2,0,0).rotateY(angle));
            angle += (float) (Math.cos(k/1.5f)/2f);
            blockPosClone = blockPos.mutableCopy().add((int) Math.round(centPos.x),(int) Math.round(centPos.y),(int) Math.round(centPos.z));
            float randumb = EOgetDatNoise.sampleNoise3D(blockPosClone.getX(),blockPosClone.getY(),blockPosClone.getZ(),30f);
            float amount = 6+randumb*5;
            for (int i = 0; i < amount; i++){
                for(int z = -1; z < 3; z++) {
                    for (int x = -1; x < 3; x++) {
                        BlockPos blockPos1 = blockPosClone.mutableCopy();
                        blockPos1 = blockPos1.add(x,i,z);
                        if((Math.abs(x-0.5d)!=1.5d||Math.abs(z-0.5d)!=1.5d||structureWorldAccess.getBlockState(blockPos1).getBlock() != Blocks.BEDROCK)){
                            float randumbest = EOgetDatNoise.sampleNoise3D(blockPos1.getX(),blockPos1.getY(),blockPos1.getZ(),14f);
                            if(randumb>0.6 && i==0){
                                if(randumbest<0.4){
                                    structureWorldAccess.setBlockState(blockPos1, Blocks.DEEPSLATE.getDefaultState(), Block.NOTIFY_LISTENERS);
                                }
                                else if(randumbest>0.8){
                                    structureWorldAccess.setBlockState(blockPos1, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                                }

                            }
                            else{
                                if(structureWorldAccess.getBlockState(blockPos1).getBlock() instanceof FluidBlock){
                                    structureWorldAccess.setBlockState(blockPos1, Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_LISTENERS);
                                }
                                else{
                                    structureWorldAccess.setBlockState(blockPos1, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                                }
                            }
                        }


                    }
                }

            }
            returnwhat = true;

        }


        return returnwhat;
    }
}

