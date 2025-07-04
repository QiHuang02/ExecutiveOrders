package net.atired.executiveorders.blocks;

import net.atired.executiveorders.init.EOParticlesInit;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.awt.*;

public class BedrockLeavesBlock extends TranslucentBlock {
    public BedrockLeavesBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(!world.getBlockState(pos.add(0,-1,0)).isSolid()&&random.nextFloat()>0.8f){
            int col= Color.HSBtoRGB(1,0,0.1f);
            if(pos.getY()>124){
                float otherNoise = MathHelper.clamp((EOgetDatNoise.sampleNoise3D(pos.getX(),pos.getY(),pos.getZ(),5)+3.14f+0.8f)/2/3.14f,0f,1f);
                otherNoise = 1-Math.max(otherNoise-0.6f,0f)*2.5f;

                float yTrue = MathHelper.clamp((pos.getY()-124+(EOgetDatNoise.sampleNoise3D(pos.getX(),0,pos.getZ(),30))*8f)/40f,0f,1f);
                col= Color.HSBtoRGB(1-0.2f*yTrue,yTrue/1.5f*otherNoise,MathHelper.clamp(0.1f+yTrue*0.9f/(otherNoise+0.1f),0,1f));
            }

            Vec3d posd = pos.toCenterPos().add((Math.random()-0.5)/2.0,-0.5,(Math.random()-0.5)/2.0);
            world.addParticle(EOParticlesInit.BEDROCKLEAF_PARTICLE,posd.x,posd.y,posd.z,(float)((col&0xFF0000) >> 16)/0xFF,(float)((col&0x00FF00) >> 8)/0xFF,(float)(col&0x0000FF)/0xFF);
        }
        super.randomDisplayTick(state, world, pos, random);
    }
}
