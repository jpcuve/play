#version 400

uniform mat3 un_Screen;
uniform vec2 un_SpriteScreenPosition;
uniform vec2 un_SpriteSize;
uniform float un_Rotation;

layout(location = 0) in vec3 in_Position;

out vec2 pass_TextureCoordinate;

void main(){
    mat3 sca = mat3(un_SpriteSize.x, 0, 0, 0, un_SpriteSize.y, 0, 0, 0, 1);
    mat3 rot = mat3(cos(un_Rotation), sin(un_Rotation), 0, -sin(un_Rotation), cos(un_Rotation), 0, 0, 0, 1);
    mat3 tra = mat3(1, 0, 0, 0, 1, 0, un_SpriteScreenPosition.x, un_SpriteScreenPosition.y, 1);
    vec3 pos = un_Screen * tra * rot * sca * (in_Position - vec3(0.5, 0.5, 0));
    gl_Position = vec4(pos.xy, 0.0, 1.0); // put z at 0 and w at 1!
    pass_TextureCoordinate = in_Position.xy;
}