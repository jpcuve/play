#version 400

in vec3 vp;
in vec4 vc;

uniform mat4 matrix;

out vec4 pass_color;

void main(){
    gl_Position = matrix * vec4(vp, 1.0);
    pass_color = vc;
}