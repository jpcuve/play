#version 400

in vec3 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoordinate;

out vec4 pass_Color;
out vec2 pass_TextureCoordinate;

void main(){
    gl_Position = vec4(in_Position, 1.0);
    pass_Color = in_Color;
    pass_TextureCoordinate = in_TextureCoordinate;
}