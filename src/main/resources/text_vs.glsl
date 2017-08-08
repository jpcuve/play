#version 400

uniform mat3 un_Screen;
uniform mat2 un_Texture;
uniform vec2 un_CharacterScreenPosition;
uniform vec2 un_CharacterTexturePosition;

in vec2 in_Position;

out vec2 pass_TextureCoordinate;

void main(){
    vec3 pos = un_Screen * vec3(in_Position + un_CharacterScreenPosition, 1.0);
    gl_Position = vec4(pos.xy, 0.0, 1.0); // put z at 0 and w at 1!
    pass_TextureCoordinate = un_Texture * vec2(in_Position + un_CharacterTexturePosition);
}