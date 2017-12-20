package entities;

import models.TexturedModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.RigidBody;


public class Cloud extends Entity {

    private float TURN_SPEED;
    private float distanceFromCenter;
    private float angleAroundCenter;
    private  int turn_direction;
    private Vector3f center;
    private RigidBody body;

    public Cloud(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f center) {
        super(model, position, rotX, rotY, rotZ, scale);
        this.center = center;
        this.turn_direction = Math.random() - Math.random() > 0? 1 : -1;
        this.distanceFromCenter =  new Vector3f(center.x, position.y, center.z).distance(position);
        this.angleAroundCenter = (float) (Math.random() * 360);
        this.TURN_SPEED = 0.5f + (float)Math.random();

    }

    public void update (float interval) {
        angleAroundCenter += interval * turn_direction * TURN_SPEED;
        updateCloudPosition();
        this.body.addPosition(this.position);
    }

    private void updateCloudPosition(){

        float theta =  angleAroundCenter;
        float offsetX = (float) (distanceFromCenter * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (distanceFromCenter * Math.cos(Math.toRadians(theta)));

        Vector3f newPosition = new Vector3f(center.x - offsetX, this.position.y, center.z - offsetZ);
        super.updatePosition(newPosition);
    }


    public void setBody(RigidBody body){
        this.body = body;
    }
}

