#version 150
// hookshot_beam.fsh — emissive chain with scrolling UV
uniform sampler2D Sampler0;
uniform float GameTime;
in vec2 vUV;
out vec4 fragColor;
void main(){
    vec2 uv = vUV;
    uv.y += GameTime * 1.2; // scroll
    vec4 tex = texture(Sampler0, uv);
    float alpha = tex.a * (0.7 + 0.3 * sin(GameTime*8.0));
    fragColor = vec4(tex.rgb * 1.3, alpha);
    if(fragColor.a < 0.05) discard;
}
