package entities;

import Factories.Factory;
import audio.AudioMaster;
import audio.Source;
import inputs.KeyboardInput;
import models.TexturedModel;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;


public class Player extends Entity {
    private KeyboardInput keyboardInput;
    private Factory factory = Factory.getInstance();
    private Light jetLight;
    private int MAX_SHOTS = 1000;
    private boolean shooting = false;
    private RigidBody body;
    private Camera camera;




    //private RigidBody
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);

        entityDescription = "The player";
        this.keyboardInput = KeyboardInput.getInstance();
        this.body = new RigidBody(PhysicsEngine.OBJECT_SPHERE, this, 999);
        this.camera = new Camera(this);
    }

    public void update () {
        camera.update();

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
        camera.input();
        if(keyboardInput.isKeyPressed(GLFW_KEY_C)){
            if(MAX_SHOTS > 0) {
                MAX_SHOTS--;
                PhysicsEngine.getInstance().soundShoot();
                shot();
            }
        }
    }

    private void shot(){
        if (!shooting){
            shooting = true;
            Vector3f old_position = new Vector3f(this.position);
            old_position.y += 1;
            float distance = 1.5f;


            float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
            float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));


            float yPos =  0.01f;
            if(getCamera().getPitch() < camera.DEFAULT_PITCH){
                yPos += camera.DEFAULT_PITCH - getCamera().getPitch();
                yPos *=0.02;
            }

            Vector3f new_position = new Vector3f(old_position.x + dx, old_position.y + yPos  ,old_position.z +dz);
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

    public Camera getCamera(){
        return camera;
    }
}
