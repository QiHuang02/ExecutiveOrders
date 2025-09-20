#version 150
uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D ParSampler;
uniform sampler2D ParSampler2;
in vec2 texCoord;
in vec2 oneTexel;
uniform vec2 InSize;
uniform float GameTime;
uniform float _FOV;

out vec4 fragColor;
const float near = 0.01;
const float far = 100.0;
float LinearizeDepth(float depth)
{
    float z = depth * 2.0f - 1.0f;
    return (near * far) / (far + near - z * (far - near));
}
vec4 sobel(vec2 offsetex) {
    float kernel[9] = float[](1, 2, 1, 0, 0, 0, -1, -2, -1);

    vec2 mosaicInSize =oneTexel*6;
    vec2 offset[9] = vec2[](
    vec2(-mosaicInSize.x, mosaicInSize.y),
    vec2(0, mosaicInSize.y),
    vec2(mosaicInSize.x, mosaicInSize.y),
    vec2(-mosaicInSize.x, 0),
    vec2(0, 0),
    vec2(mosaicInSize.x, 0),
    vec2(-mosaicInSize.x, -mosaicInSize.y),
    vec2(0, -mosaicInSize.y),
    vec2(mosaicInSize.x, -mosaicInSize.y)
    );

    float Gx = 0.0;
    float Gy = 0.0;
    for (int i = 0; i < 9; i++) {
        float intensity;
        vec2 coord = offsetex + offset[i];
        coord.x = clamp(coord.x, 0, 1);
        coord.y = clamp(coord.y, 0, 1);
        vec3 sampleVar = texture(ParSampler, coord).rgb*texture(ParSampler2, coord).rgb+texture(DiffuseSampler,coord).rgb/8;
        sampleVar = vec3(clamp(sampleVar.r,0,3.14),clamp(sampleVar.g,0,3.14),clamp(sampleVar.b,0,3.14));
        intensity = (sampleVar.r + sampleVar.g + sampleVar.b) / 3.0;

        if (i != 4) {
            Gx += intensity * kernel[i];
        }
        int j = (i % 3) * 3 + i / 3;
        if (j != 4) {
            Gy += intensity * kernel[j];
        }
    }
    float G = sqrt(Gx * Gx + Gy * Gy);
    float edgeThreshold = 0.3;
    float alpha = G > edgeThreshold ? 1.0 : 0.0;
    return vec4(G, G, G, alpha);
}
void main() {
    float depth = LinearizeDepth(texture(DepthSampler, texCoord).r);

    float distance = length(vec3(1., (2.*texCoord - 1.) * vec2(InSize.x/InSize.y,1.) * tan(radians(_FOV / 2.))) * depth);

    vec2 mosaicInSize = InSize / 4;
    vec2 offsettex = texCoord;
    offsettex-=fract(offsettex*mosaicInSize)/mosaicInSize;
    if((texture(ParSampler,offsettex).r<1.0|| texture(ParSampler2,offsettex).r<1.0)&&texture(DiffuseDepthSampler, texCoord).r>0.98){
        fragColor = vec4(vec3(1,1,1)*sobel(offsettex).a,clamp(texture(ParSampler,offsettex).a+texture(ParSampler2,offsettex).a,0,1f));
    }
    else{
        fragColor = texture(DiffuseSampler,texCoord);
        fragColor.a = 1.0f;
    }

}