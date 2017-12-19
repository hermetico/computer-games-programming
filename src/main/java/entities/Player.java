package entities;

import inputs.KeyboardInput;
import models.TexturedModel;
import org.joml.Vector3f;
import physics.RigidBody;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;


public class Player extends Entity {
    private KeyboardInput keyboardInput;
    private EntityFactory  factory = EntityFactory.getInstance();
    private Light jetLight;
    private int MAX_SHOTS = 1000;
    private boolean shooting = false;
    //private RigidBody
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);

        entityDescription = "The player";
        this.keyboardInput = KeyboardInput.getInstance();
    }

    public void update () {
        if(jetLight != null){
            Vector3f lightPos = new Vector3f(super.getPosition());
            lightPos.y = super.getAABB().getMin().y;

            jetLight.setPosition(lightPos);
       }

    }

    public void setJetLight(Light light){
        this.jetLight = light;
    }

    public void input(){
        if(keyboardInput.isKeyPressed(GLFW_KEY_S)){
            if(MAX_SHOTS > 0) {
                MAX_SHOTS--;
                shot();
            }
        }
    }

    private void shot(){
        if (!shooting){
            shooting = true;
            System.out.println("Shooting");
            Vector3f position = new Vector3f(this.position);
            factory.createBullet(position, position);
        }

    }

    public void shooting_allowed(){
        shooting = false;


    }
}
