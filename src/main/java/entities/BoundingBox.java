package entities;

import org.joml.Vector3f;

public class BoundingBox {

    // lines
    public  static float[] boundingPositions = new float[]{

            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f,0.5f, -0.5f,

            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f,-0.5f, -0.5f,


    };
    public static int[] boundingIndices = new int[]{
            // Top square
            0, 1, 1, 2, 2, 3, 3, 0,
            // bottom square
            4, 5, 5, 6, 6, 7, 7, 4,

            // vertical edges
            0, 4, 1, 5, 2, 6, 3, 7
    };

    private int VAOID = 0;
    public BoundingBox(int vaoID){
        this.VAOID =vaoID;
    }
    private Vector3f scale = new Vector3f(1,1,1);
    private Vector3f position = new Vector3f(0,0,0);
    private Vector3f rotation = new Vector3f(0,0,0);

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

    public void setBoundingPositions(float[] boundingPositions) {
        this.boundingPositions = boundingPositions;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
}
