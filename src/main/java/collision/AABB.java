package collision;

import entities.Entity;
import org.joml.Vector3f;

public class AABB {

    private Vector3f pos;
    private float xOffset = 0;
    private float yOffset = 0;
    private float zOffset = 0;
    private float w;
    private float l;
    private float h;
    private float r;
    private int size;

    public AABB(Vector3f pos, int w, int l, int h){
        this.pos = pos;
        this.w = w;
        this.l = l;
        this.h = h;

        size = Math.max(w, Math.max(l, h));
    }

    public AABB(Vector3f pos, int r, Entity e){
        this.pos = pos;
        this.r = r;

        this.size = r;
    }

    public Vector3f getPos() {
        return pos;
    }

    public float getRadius() {
        return r;
    }

    public float getWidth() {
        return w;
    }

    public float getLength() {
        return l;
    }

    public float getHeigth() {
        return h;
    }

    public void setBox(Vector3f pos, int w, int l, int h){
        this.pos = pos;
        this.w = w;
        this.l = l;
        this.h = h;

        size = Math.max(w, Math.max(l, h));
    }

    public void setCircle(Vector3f pos, int r){
        this.pos = pos;
        this.r = r;

        this.size = r;
    }

    public void setH(float h) {
        this.h = h;
    }

    public void setL(float l) {
        this.l = l;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public void setzOffset(float zOffset) {
        this.zOffset = zOffset;
    }

    public boolean collides( AABB bbox){
        float ax = pos.x;
        float ay = pos.y;
        float az = pos.z;

        float bx = bbox.pos.x;
        float by = bbox.pos.y;
        float bz = bbox.pos.z;

        if (Math.abs(ax - bx) < (5) && Math.abs(ay - by) < (10) && Math.abs(az - bz) < (5)){

            return true;
        }
        return false;
    }
}
