#version 150
// hookshot_beam.vsh — chain trail billboard
#version 150
in vec3 Position;
in vec2 UV0;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
out vec2 vUV;
void main(){
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vUV = UV0;
}
