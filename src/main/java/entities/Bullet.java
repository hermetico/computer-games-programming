package entities;

import models.TexturedModel;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;

public class Bullet extends Entity{
    private RigidBody body;
    public Bullet(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f old_position) {
        super(model, position, rotX, rotY, rotZ, scale);
        entityDescription = "A bullet";
        this.body = new RigidBody(PhysicsEngine.OBJECT_SPHERE, this, 999);
        body.setOldPosition(old_position);
    }

    public void update () {


    }

    public RigidBody getBody() {
        return body;
    }
}
