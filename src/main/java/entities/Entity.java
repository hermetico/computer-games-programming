package entities;

import entities.extensions.Selectable;
import models.RawEntity;
import models.TexturedModel;
import org.joml.Vector3f;
import collision.AABB;

public class Entity implements Selectable{
    private TexturedModel model;
    protected Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;
    private int textureIndex = 0;
    protected AABB bounds;
    protected BoundingBox boundingBox;
    protected String entityDescription = "Not defined";
    protected boolean selected = false;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        adaptBoundingBox();
    }

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, String description) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.entityDescription = description;
        adaptBoundingBox();

    }

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, int textureIndex) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.textureIndex = textureIndex;

        adaptBoundingBox();
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

        adaptBoundingBox();
    }

    private void adaptBoundingBox(){
        this.boundingBox  = new BoundingBox(model.getRawEntity().getBBVAO());
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

        Vector3f size = new Vector3f(max_x-min_x, max_y-min_y, max_z-min_z);
        Vector3f center = new Vector3f((min_x+max_x)/2, (min_y+max_y)/2, (min_z+max_z)/2);

        this.boundingBox.setScale(size);
        this.boundingBox.setPosition(center.add(this.position));
        this.boundingBox.setRotation(new Vector3f(rotX, rotY, rotZ));

        //FIXME merge features boundingBox and bounds
        this.bounds = new AABB(position, 101, 101, 101);
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
        this.boundingBox.getPosition().add(dx, dy, dz);
    }

    public void increaseRotation(float dx, float dy, float dz){
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;

        this.boundingBox.getRotation().add(dx, dy, dz);
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

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    @Override
    public Boolean getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public Vector3f getBoxPosition() {
        return boundingBox.getPosition();
    }

    @Override
    public Vector3f getBoxScale() {
        return boundingBox.getScale();
    }

    @Override
    public String getEntityDescription() {
        return entityDescription;
    }

    public void setEntityDescription(String entityDescription) {
        this.entityDescription = entityDescription;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public AABB get_bounds(){ return bounds; }
}
