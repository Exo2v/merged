// lens_of_truth.vsh — OoT Lens of Truth post effect (fabric 1.21.1)
// Fullscreen quad; vertex pushes UVs, fragment does edge-detect + grayscale
#version 150
in vec3 Position;
in vec2 UV0;
uniform mat4 ProjMat;
out vec2 texCoord;
void main() {
    gl_Position = ProjMat * vec4(Position, 1.0);
    texCoord = UV0;
}
