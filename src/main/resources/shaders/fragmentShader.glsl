#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void){

    //
    // Diffuse lighting
    //
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector); // vector from vertex to light


    float nDotl = dot(unitNormal, unitLightVector);
    float brightness = max(nDotl, 0.2);
    vec3 diffuse = brightness * lightColour;
    //
    // Specular lighting
    //
    vec3 unitVectorToCamera = normalize(toCameraVector);
    // the inverse of the vector from the vertex to the light
    vec3 lightDirection = -unitLightVector;

    // reflection over the surface
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    specularFactor = max(specularFactor, 0);
    float dampedFactor = pow(specularFactor, shineDamper);

    vec3 finalSpecular = dampedFactor * reflectivity * lightColour;

    vec4 textureColour = texture(textureSampler, pass_textureCoords);
    if(textureColour.a < .5) discard;
    // returns the color of the pixel at the coordinates in pass_textureCoords
    out_Color = vec4(diffuse, 1.0) * textureColour + vec4(finalSpecular, 1.0);
    out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);

}