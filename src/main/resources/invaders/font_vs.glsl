#version 400

uniform float un_Scale;
uniform vec2 un_CharSize;
uniform vec2 un_CharScreenPosition;
uniform mat3 un_Screen;
uniform float un_Offset;
uniform float un_Total;

layout(location = 0) in vec3 in_Position;

out vec2 pass_TextureCoordinate;

void main(){
    mat3 sca = mat3(un_Scale * un_CharSize.x, 0, 0, 0, un_Scale * un_CharSize.y, 0, 0, 0, 1);
    mat3 tra = mat3(1, 0, 0, 0, 1, 0, un_CharScreenPosition.x, un_CharScreenPosition.y, 1);
    vec3 pos = un_Screen * tra * sca * in_Position;
    gl_Position = vec4(pos.xy, 0.0, 1.0); // put z at 0 and w at 1!
    mat3 tex = mat3(un_CharSize.x / un_Total, 0, 0, 0, 1, 0, un_Offset / un_Total, 0, 1);
    vec3 dis = tex * in_Position;
    pass_TextureCoordinate = dis.xy;
}