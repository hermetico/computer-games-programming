package models;

public class RawEntity extends RawModel {
    private float[] positions;
    private int bbVao;

    public RawEntity(int vaoID, int vertexCount, float[] positions){
        super(vaoID, vertexCount);
        this.positions = positions;

    }

    public float[] getPositions() {
        return positions;
    }

    public int getBBVAO() {
        return bbVao;
    }
    public void setBBVAO(int bbvao){
        this.bbVao = bbvao;
    }
}
