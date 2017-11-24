package entities;

import org.joml.Vector3f;

import java.util.Vector;

public class BoundingBox {

    // lines
    private float[] boundingPositions = new float[]{

            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f,0.5f, -0.5f,

            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f,-0.5f, -0.5f,


    };
    private int[] boundingIndices = new int[]{
            // Top square
            0, 1, 1, 2, 2, 3, 3, 0,
            // bottom square
            4, 5, 5, 6, 6, 7, 7, 4,

            // vertical edges
            0, 4, 1, 5, 2, 6, 3, 7
    };

    private int VAOID = 0;

    private Vector3f scale = new Vector3f(1,1,1);
    private Vector3f center = new Vector3f(0,0,0);

    public float[] getBoundingPositions() {
        return boundingPositions;
    }

    public int[] getBoundingIndices() {
        return boundingIndices;
    }

    public int getCount(){
        return boundingIndices.length;
    }

    public int getVAOID() {
        return VAOID;
    }

    public void setVAOID(int VAOID) {
        this.VAOID = VAOID;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }
}
