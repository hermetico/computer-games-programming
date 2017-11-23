#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColour[4];
uniform vec3 lightAttenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void){


    vec4 blendMapColour = texture(blendMap, pass_textureCoords);
    float backTectureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
    vec2 tileCoord = pass_textureCoords * 100;
    vec4 backgroundTextureColour = texture(backgroundTexture, tileCoord) * backTectureAmount;
    vec4 rTextureColour = texture(rTexture, tileCoord) * blendMapColour.r;
    vec4 gTextureColour = texture(gTexture, tileCoord) * blendMapColour.g;
    vec4 bTextureColour = texture(bTexture, tileCoord) * blendMapColour.b;

    vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
    vec3 unitNormal = normalize(surfaceNormal);


    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for(int i = 0; i < 4; i++){
        vec3 unitLightVector = normalize(toLightVector[i]); // vector from vertex to light
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.2);
        float distance = length(toLightVector[i]);
        float attFactor = lightAttenuation[i].x + (lightAttenuation[i].y * distance) + (lightAttenuation[i].z * distance * distance);

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

        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attFactor;
        totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attFactor;
    }
    // returns the color of the pixel at the coordinates in pass_textureCoords
    out_Color = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}