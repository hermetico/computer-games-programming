package entities;

import models.TexturedModel;
import org.joml.Vector3f;

public class Player extends Entity {


    private Light jetLight;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);

        entityDescription = "The player";
    }

    public void update () {
        if(jetLight != null){
            Vector3f lightOffset = new Vector3f(0, super.getAABB().getMin().y, 0);
            jetLight.setPosition(new Vector3f(super.getPosition()).add(lightOffset));
       }

    }

    public void setJetLight(Light light){
        this.jetLight = light;
    }
}
