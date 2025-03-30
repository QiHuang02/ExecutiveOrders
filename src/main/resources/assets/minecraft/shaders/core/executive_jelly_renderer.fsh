#version 150

#moj_import <fog.glsl>
uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;
float rand(vec2 co){
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}
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
void main() {
    vec4 color = texture(Sampler0, texCoord0);


    color *= vertexColor * ColorModulator;
    vec3 hsvcol = rgb2hsv(color.xyz);
    color.b *= 1.5;
    color.b = clamp(color.b,0,1);
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    float yeah = sin((gl_FragCoord.y-fract(gl_FragCoord.y/16)*16) /32*pow(vertexDistance,0.5) + GameTime * 3.1415926535 * 1024.0 + cos((gl_FragCoord.x-fract(gl_FragCoord.x/16)*16) /16+ GameTime * 2048.0)/2);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    if(yeah>0.3){
        fragColor.a = clamp(fragColor.a-yeah*0.7,0,1);
    }

}
