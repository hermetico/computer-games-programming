package entities;

import inputs.KeyboardInput;
import models.TexturedModel;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;


public class Player extends Entity {
    private KeyboardInput keyboardInput;
    private EntityFactory  factory = EntityFactory.getInstance();
    private Light jetLight;
    private int MAX_SHOTS = 1000;
    private boolean shooting = false;
    private RigidBody body;
    //private RigidBody
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);

        entityDescription = "The player";
        this.keyboardInput = KeyboardInput.getInstance();
        this.body = new RigidBody(PhysicsEngine.OBJECT_SPHERE, this, 999);
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
            Vector3f old_position = new Vector3f(this.position);
            float distance = 1f;


            float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
            float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));
            Vector3f new_position = new Vector3f(old_position.x + dx, old_position.y + 0.2f ,old_position.z +dz);
            factory.createBullet(new_position, old_position);
        }

    }

    public void shooting_allowed(){
        shooting = false;
    }

    public RigidBody getBody() {
        return body;
    }

    public Light getJetLight() {
        return jetLight;
    }
}
