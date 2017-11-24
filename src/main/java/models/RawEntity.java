package models;

import entities.BoundingBox;

public class RawEntity extends RawModel {
    private float[] positions;
    private BoundingBox boundingBox;

    public RawEntity(int vaoID, int vertexCount, float[] positions){
        super(vaoID, vertexCount);
        this.positions = positions;
        this.boundingBox = new BoundingBox();
    }

    public float[] getPositions() {
        return positions;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
