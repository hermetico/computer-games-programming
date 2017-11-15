#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void){


    vec4 blendMapColour = texture(blendMap, pass_textureCoords);
    float backTectureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
    vec2 tileCoord = pass_textureCoords * 200;
    vec4 backgroundTextureColour = texture(backgroundTexture, tileCoord) * backTectureAmount;
    vec4 rTextureColour = texture(rTexture, tileCoord) * blendMapColour.r;
    vec4 gTextureColour = texture(gTexture, tileCoord) * blendMapColour.g;
    vec4 bTextureColour = texture(bTexture, tileCoord) * blendMapColour.b;

    vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
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

    // returns the color of the pixel at the coordinates in pass_textureCoords
    out_Color = vec4(diffuse, 1.0) * totalColour + vec4(finalSpecular, 1.0);
    out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}