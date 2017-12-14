package entities;

import entities.extensions.Selectable;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import physics.AABB;
import utils.Maths;

public class Entity implements Selectable{
    private TexturedModel model;
    protected Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;
    private int textureIndex = 0;
    protected collision.AABB bounds;
    protected AABB AABB;
    protected String entityDescription = "Not defined";
    protected boolean selected = false;
    protected boolean debugSelected = false;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        createBoundingBox();
    }

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, String description) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.entityDescription = description;
        createBoundingBox();

    }

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, int textureIndex) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.textureIndex = textureIndex;

        createBoundingBox();
    }

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, int textureIndex, String description) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.textureIndex = textureIndex;
        this.entityDescription = description;

        createBoundingBox();
    }

    private void createBoundingBox(){
        this.AABB = new AABB(model.getRawEntity().getBBVAO(), scale);
        float min_x, max_x, min_y, max_y, min_z, max_z;

        float[] vertices = model.getRawEntity().getPositions();
        min_x = max_x = vertices[0];
        min_y = max_y = vertices[1];
        min_z = max_z = vertices[2];
        
        for (int i = 0; i < vertices.length; i +=3) {
            if (vertices[i] < min_x) min_x = vertices[i];
            if (vertices[i] > max_x) max_x = vertices[i];

            if (vertices[i+1] < min_y) min_y = vertices[i+1];
            if (vertices[i+1] > max_y) max_y = vertices[i+1];

            if (vertices[i+2] < min_z) min_z = vertices[i+2];
            if (vertices[i+2] > max_z) max_z = vertices[i+2];
        }

        Vector3f size = new Vector3f((max_x-min_x) , (max_y-min_y) , (max_z-min_z) );
        Vector3f center = new Vector3f((min_x+max_x)/2, (min_y+max_y)/2, (min_z+max_z)/2);

        System.out.println("Entity size:");
        System.out.println(size);

        System.out.println("Entity min:");
        System.out.println(new Vector3f(min_x, min_y, min_z));

        this.AABB.setSize(size);

        this.AABB.updatePosition(center.add(this.position));
        this.bounds = new collision.AABB(position, 101, 101, 101);

    }

    private void adaptBoundingBox(){
        Matrix4f rotationMatrix = Maths.createAABBTransformationMatrix(this.rotX,this.rotY, this.rotZ);
        Vector3f ratio = this.AABB.getSizeRatio();


        float min_x, max_x, min_y, max_y, min_z, max_z;
        float[] vertices = this.AABB.boundingPositions;

        Vector4f position = rotationMatrix.transform(new Vector4f(vertices[0],vertices[1], vertices[2], 0));

        min_x = max_x = position.x;
        min_y = max_y = position.y;
        min_z = max_z = position.z;

        for (int i = 0; i < vertices.length; i +=3) {
            position = rotationMatrix.transform(new Vector4f(vertices[i], vertices[i+1], vertices[i+2], 0));
            if (position.x < min_x) min_x = position.x;
            if (position.x > max_x) max_x = position.x;

            if (position.y < min_y) min_y = position.y;
            if (position.y > max_y) max_y = position.y;

            if (position.z < min_z) min_z = position.z;
            if (position.z > max_z) max_z = position.z;
        }

        Vector3f size = new Vector3f((max_x-min_x) * ratio.x, (max_y-min_y) *  ratio.y, (max_z-min_z) * ratio.z);

        System.out.println("Entity size:");
        System.out.println(size.x + " " + size.y + " " + size.z);

        System.out.println("Entity min:");
        System.out.println(new Vector3f(min_x, min_y, min_z));

        this.AABB.updateSize(size);
    }

    protected boolean landingOnTerrain( float terrainHeight){
        if (position.y < terrainHeight) {
            float diff = terrainHeight - position.y;
            increasePosition(0, diff, 0);
            return true;
        }

        return false;
    }

    public float getTextureXOffset(){
        int column = textureIndex % model.getTexture().getNumberOfRows();
        return (float) column / (float)model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset(){
        int row = textureIndex / model.getTexture().getNumberOfRows();
        return (float) row / (float)model.getTexture().getNumberOfRows();
    }

    public void increasePosition(float dx, float dy, float dz){
        this.position.add(dx, dy, dz);
        this.AABB.updatePosition(dx, dy, dz);
    }

    public void increaseRotation(float dx, float dy, float dz){
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
        this.adaptBoundingBox();

    }

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }


    public float getRotY() {
        return rotY;
    }


    public float getRotZ() {
        return rotZ;
    }


    @Override
    public Boolean getSelected() {
        return selected;
    }

    @Override
    public Boolean getDebugSelected() {
        return debugSelected;
    }

    @Override
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public void setDebugSelected(Boolean selected) {
        this.debugSelected = selected;
    }

    @Override
    public Vector3f getBoxPosition() {
        return AABB.getPosition();
    }

    @Override
    public Vector3f getBoxScale() {
        return AABB.getScale();
    }

    @Override
    public String getEntityDescription() {
        return entityDescription;
    }

    @Override
    public AABB getAABB() {
        return AABB;
    }
    public void setEntityDescription(String entityDescription) {
        this.entityDescription = entityDescription;
    }

    public float getScale() {
        return scale;
    }





    public collision.AABB get_bounds(){ return bounds; }
}
