package net.atired.executiveorders.misc;
//Based on the math class from Alex's Caves
public class EOgetDatNoise {

    public static float sampleNoise2D(int x, int z, float simplexSampleRate) {
        return (float) ((EOsimplexNoise.noise((x + simplexSampleRate) / simplexSampleRate, (z + simplexSampleRate) / simplexSampleRate)));
    }

    public static float sampleNoise3D(int x, int y, int z, float simplexSampleRate) {
        return (float) ((EOsimplexNoise.noise((x + simplexSampleRate) / simplexSampleRate, (y + simplexSampleRate) / simplexSampleRate, (z + simplexSampleRate) / simplexSampleRate)));
    }

    public static float sampleNoise3D(float x, float y, float z, float simplexSampleRate) {
        return (float) ((EOsimplexNoise.noise((x + simplexSampleRate) / simplexSampleRate, (y + simplexSampleRate) / simplexSampleRate, (z + simplexSampleRate) / simplexSampleRate)));
    }


}
