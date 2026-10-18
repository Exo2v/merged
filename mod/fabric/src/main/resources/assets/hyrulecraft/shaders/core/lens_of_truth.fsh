#version 150
// lens_of_truth.fsh — grayscale + shimmer + hidden-block outline
// Activated when player holds LensOfTruthItem (checked in HyrulecraftClient post processor)
// Inspired by OoT's lens vignette + blue tint
uniform sampler2D InSampler;
uniform float GameTime; // seconds
uniform vec2 InSize;
in vec2 texCoord;
out vec4 fragColor;

float luminance(vec3 c){ return dot(c, vec3(0.299, 0.587, 0.114)); }

void main(){
    vec3 col = texture(InSampler, texCoord).rgb;
    float lum = luminance(col);

    // Desaturate but keep blue hint
    vec3 desat = vec3(lum) * 0.85 + col * 0.15;
    desat = mix(desat, vec3(0.45, 0.68, 0.95), 0.12); // blue tint

    // Edge detect via 3x3 Sobel-ish (cheap)
    float e = 0.0;
    vec2 px = 1.0 / InSize;
    for(int x=-1;x<=1;x++) for(int y=-1;y<=1;y++){
        if(x==0&&y==0) continue;
        float n = luminance(texture(InSampler, texCoord + vec2(x,y)*px).rgb);
        e += abs(lum - n);
    }
    e = clamp(e * 1.8, 0.0, 1.0);

    // Vignette (OoT lens border)
    vec2 uv = texCoord * 2.0 - 1.0;
    float vign = 1.0 - dot(uv, uv) * 0.16;
    vign = clamp(vign, 0.0, 1.0);

    // Shimmer pulse
    float shimmer = sin(GameTime*2.4 + texCoord.x*16.0) * 0.04;

    vec3 outCol = desat * vign + vec3(e) * 0.55;
    outCol += shimmer;

    fragColor = vec4(outCol, 1.0);
}
