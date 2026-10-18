#version 120
// gbuffers_terrain.fsh — adds warm OoT dusk tint + cracked_stone emissive
uniform sampler2D gtexture;
uniform sampler2D lightmap;
varying vec2 texcoord;
varying vec4 glcolor;
void main(){
  vec4 color = texture2D(gtexture, texcoord) * glcolor;
  color *= texture2D(lightmap, vec2(0.8,0.9));
  // Cracked stone faint glow (for dungeons) — detect as mid-gray
  float crack = step(0.52, color.r) * step(color.r, 0.58);
  color.rgb += crack * vec3(0.12, 0.08, 0.02);
  gl_FragData[0] = color;
}
