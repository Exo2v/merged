// gbuffers_terrain.vsh — Hyrulecraft Iris pass (minimal)
// Leave vanilla terrain but tint fog with OoT sunset when Sun's Song active
#version 120
varying vec2 texcoord;
varying vec4 glcolor;
void main(){
  gl_Position = ftransform();
  texcoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
  glcolor = gl_Color;
}
