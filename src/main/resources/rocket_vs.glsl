#version 400

in vec3 in_Position;

uniform mat4 un_Model;
uniform mat4 un_View;
uniform mat4 un_Projection;

out vec4 pass_Color;

void main(){
    gl_Position = un_Projection * un_View * un_Model * vec4(in_Position, 1.0);
    gl_PointSize = 15.0;
    pass_Color = vec4(1, 1, 1, 1);
}