#version 150
// ztarget_reticle.fsh — pulsing gold diamond
uniform float GameTime;
in vec4 vColor;
in vec2 vUV;
out vec4 fragColor;
void main(){
    float pulse = 0.7 + 0.3 * sin(GameTime * 4.0);
    vec4 c = vColor;
    c.rgb *= pulse;
    // circular cutout
    vec2 d = vUV - 0.5;
    float dist = length(d);
    if(dist > 0.48) discard;
    fragColor = c;
}
