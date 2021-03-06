#version 400

in vec3 in_Position;
in vec4 in_Color;

uniform mat4 un_Model;
uniform mat4 un_View;
uniform mat4 un_Projection;


out vec4 pass_Color;
out vec2 pass_TextureCoordinate;

void main(){
    gl_Position = un_Projection * un_View * un_Model * vec4(in_Position, 1.0);
    pass_Color = in_Color;
}