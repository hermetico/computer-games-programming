package entities;

import models.TexturedModel;
import org.joml.Vector3f;
import utils.KeyboardInput;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private KeyboardInput keyboardInput;
    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED  = 160;
    private static final float yOffset = -90; // model y rotation offset
    private static float GRAVITY = -80;
    private static final float JUMP_POWER = 45;


    private static final float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private boolean alreadyJumping = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        keyboardInput = KeyboardInput.getInstance();
    }

    public void update ( float interval){
        super.increaseRotation(0, currentTurnSpeed * interval, 0);
        float distance = currentSpeed * interval;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY() + yOffset)));
        float dz =(float) (distance * Math.cos(Math.toRadians(super.getRotY() + yOffset)));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * interval;
        super.increasePosition(0, upwardsSpeed*interval, 0);
        if (super.getPosition().y < TERRAIN_HEIGHT){
            upwardsSpeed = 0;
            alreadyJumping = false;
            super.getPosition().y = TERRAIN_HEIGHT;
        }


    }
    public void input(){
        checkInputs();
    }

    private void jump(){
        if(!alreadyJumping) {
            this.upwardsSpeed = JUMP_POWER;
            alreadyJumping = true;
        }
    }
    private void checkInputs(){
        if (keyboardInput.isKeyPressed(GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        }else if (keyboardInput.isKeyPressed(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        }else{
            this.currentSpeed = 0;
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        }else if (keyboardInput.isKeyPressed(GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        }else{
            this.currentTurnSpeed = 0;
        }

        if(keyboardInput.isKeyPressed(GLFW_KEY_SPACE)){
            jump();
        }

    }
    public float getyOffset(){
        return yOffset;
    }
}
