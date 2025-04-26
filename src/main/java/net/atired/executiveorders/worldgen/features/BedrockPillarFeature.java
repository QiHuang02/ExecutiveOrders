package net.atired.executiveorders.worldgen.features;

import com.mojang.serialization.Codec;
import net.atired.executiveorders.init.EOBlocksInit;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
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
        BlockPos.Mutable blockPos2 = blockPos.mutableCopy();
        float randumb = EOgetDatNoise.sampleNoise3D(blockPos.getX(),blockPos.getY(),blockPos.getZ(),300f);
        if(randumb>0.50f && (blockPos.getX()%3 == 0)&&(blockPos.getZ()%3 == 0) && structureWorldAccess.getBlockState(blockPos1.add(0,-1,0)).getBlock() == Blocks.BEDROCK){
            float randumber = (float) (Math.random()*3.14f*2);
            boolean leaves = false;
            float amount = 16*((randumb-0.5f)*10);
            if(Math.random()>0.87 && randumb>0.6&&randumb<0.7){
                amount+=10;
                leaves = true;
            }
            for (int i = 0; i < amount; i++){
                structureWorldAccess.setBlockState(blockPos1, Blocks.BEDROCK.getDefaultState(), Block.NOTIFY_LISTENERS);
                blockPos1 = blockPos1.move(Direction.UP);
            }
            if(Math.random()>0.8&&amount>40&&!leaves){
                BlockPos.Mutable blockPos3 = blockPos2.mutableCopy();
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        blockPos3 = blockPos3.set(blockPos2.getX()+x,blockPos2.getY()+amount,+blockPos2.getZ()+z);
                        int i = Math.abs(x) + Math.abs(z);
                        if(structureWorldAccess.getBlockState(blockPos3).isAir() && i <4){
                            structureWorldAccess.setBlockState(blockPos3, Blocks.BEDROCK.getDefaultState(), Block.NOTIFY_LISTENERS);

                        }

                    }
                }
            }
            if(leaves){
                blockPos2 = blockPos2.move(0,10,0);
                for(int i = 0; i < amount-10; i++){
                    Vec3d vec3d = new Vec3d(Math.pow((amount-10-i),0.5),i,0).rotateY(i/2.5f+randumber);
                    BlockPos.Mutable blockPos3 = blockPos2.mutableCopy();

                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {
                                blockPos3 = blockPos3.set(vec3d.getX()+blockPos2.getX()+x,vec3d.getY()+blockPos2.getY()+y,vec3d.getZ()+blockPos2.getZ()+z);
                                if(structureWorldAccess.getBlockState(blockPos3).isAir())
                                    structureWorldAccess.setBlockState(blockPos3, EOBlocksInit.BEDROCK_LEAVES.getDefaultState(), Block.NOTIFY_LISTENERS);

                            }
                        }
                    }

                }
            }
            return true;
        }

        return false;
    }
}

