package physics;

import org.joml.Vector3f;

public class AABB {

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
    private float scale;
    private Vector3f size = new Vector3f(1,1,1);
    private Vector3f center = new Vector3f(0,0,0);
    private Vector3f rotation = new Vector3f(0,0,0);
    private Vector3f min = new Vector3f(-1f,-1f, -1f);
    private Vector3f max = new Vector3f(1f, 1f, 1f);



    public Vector3f sizeRatio = new Vector3f(1,1,1);
    public AABB(int vaoID, float scale)
    {
        this.scale = scale;
        this.VAOID = vaoID;
    }




    public int getCount(){
        return boundingIndices.length;
    }

    public int getVAOID() {
        return VAOID;
    }


    public void setSize(Vector3f size) {

        this.size = new Vector3f(this.size).mul(size);
        this.sizeRatio.mul(size);

        //System.out.println("AABB size:");
        //System.out.println(size);

    }

    public void updateSize(Vector3f size){
        this.size = new Vector3f(size);

        //System.out.println("AABB size:");
        //System.out.println(size);
    }

    public Vector3f getPosition() {
        return new Vector3f(center);
    }

    public void updatePosition(Vector3f offset) {
        this.center.add(offset);
    }
    public void updatePosition(float dx, float dy, float dz) {
        this.updatePosition(new Vector3f(dx, dy, dz));
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getSize() {
        return this.size;
    }

    public Vector3f getScale() {
        return new Vector3f(this.size).mul(scale);

    }
    public Vector3f getSizeRatio() {
        return sizeRatio;
    }

    public Vector3f getMin() {
        return new Vector3f(min);
    }

    public Vector3f getMax() {
        return new Vector3f(max);
    }
}
