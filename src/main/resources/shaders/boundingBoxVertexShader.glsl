#version 400 core

in vec3 position;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

const float density = 0.0035;
const float gradient = 5.0;

void main()
{
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    gl_Position =  projectionMatrix * positionRelativeToCam;
}
