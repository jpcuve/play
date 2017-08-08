#version 400

uniform sampler2D texture_diffuse;

in vec2 pass_TextureCoordinate;

out vec4 out_Color;

void main(){
    out_Color = texture(texture_diffuse, pass_TextureCoordinate);
    // out_Color = vec4(0.5, 0.5, 0.5, 0.5);
}