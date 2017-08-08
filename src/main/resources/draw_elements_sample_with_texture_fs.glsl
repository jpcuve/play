#version 400

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoordinate;

out vec4 out_Color;

void main(){
    out_Color = pass_Color;
    out_Color = texture(texture_diffuse, pass_TextureCoordinate);
}