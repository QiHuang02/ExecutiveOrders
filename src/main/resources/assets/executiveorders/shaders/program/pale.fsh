#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float GameTime;
uniform float Resolution;
uniform float Saturation;
uniform float MosaicSize;
uniform float Fade;
vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}
out vec4 fragColor;

void main() {
    vec2 mosaicInSize = InSize / MosaicSize;
    float Reso2tion = Resolution;
    vec2 fractPix = fract((texCoord+sin(GameTime*30000)/3) * mosaicInSize) / mosaicInSize;

    vec4 baseTexel = texture(DiffuseSampler, texCoord+cos(GameTime*3000+texCoord*20)/400*Fade+sin(texCoord*40)/200*Fade - fractPix);
    vec4 baseCopy = texture(DiffuseSampler,texCoord+cos(GameTime*3000+texCoord*20)/400*Fade+sin(texCoord*40)/200*Fade);
    vec3 hsvTexel = rgb2hsv(baseTexel.rgb);
    float offset =  0.5-(min(abs(0.55 - hsvTexel.x),abs(hsvTexel.x+0.45))*2);
    hsvTexel.y *= offset/2;
    hsvTexel.z = pow(hsvTexel.z*0.9,1.6)*4*clamp(offset,0.25,1);
    baseTexel.rgb = hsv2rgb(hsvTexel);
    vec3 fractTexel = baseTexel.rgb - fract(baseTexel.rgb * Reso2tion) / Reso2tion;

    float luma = dot(fractTexel, vec3(0.33, 0.33, 0.33));
    vec3 chroma = (fractTexel - luma) * Saturation*1.4;
    baseTexel.rgb = luma + chroma;
    baseTexel.a = 1.0;

    fragColor = baseTexel*Fade+baseCopy*(1-Fade);
}
