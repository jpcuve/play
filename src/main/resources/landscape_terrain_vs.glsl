#version 400

in vec3 in_Position;

uniform mat4 un_View;
uniform mat4 un_Projection;


out vec4 pass_Color;

void main(){
    gl_Position = un_Projection * un_View * vec4(in_Position, 1.0);
    pass_Color = vec4(0, 0, in_Position[1] / 7, 1);
}