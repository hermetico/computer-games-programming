package entities;

import models.TexturedModel;
import org.joml.Vector3f;

public class Bullet extends Entity{
    public Bullet(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        entityDescription = "A bullet";

    }

    public void update () {


    }
}
