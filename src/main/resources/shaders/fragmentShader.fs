#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;

void main(void){

    // returns the color of the pixel at the coordinates in pass_textureCoords
    out_Color = texture(textureSampler, pass_textureCoords);

}