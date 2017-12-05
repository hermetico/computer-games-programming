package models;

import textures.ModelTexture;

public class TexturedModel {
    private RawEntity rawEntity;
    private ModelTexture texture;

    public TexturedModel(RawEntity model, ModelTexture texture){
        this.rawEntity = model;
        this.texture = texture;
    }

    public RawEntity getRawEntity() {
        return rawEntity;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public void setTexture(ModelTexture texture) {
        this.texture = texture;
    }
    public void setRawModel(RawModel model){
        this.rawModel = model;
    }
}
