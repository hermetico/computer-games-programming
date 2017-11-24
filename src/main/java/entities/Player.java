package entities;

import models.TexturedModel;
import org.joml.Vector3f;
import terrains.Terrain;
import inputs.KeyboardInput;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private KeyboardInput keyboardInput;
    private static final float RUN_SPEED = 50;
    private static final float TURN_SPEED  = 160;
    private static final float yOffset = -90; // model y rotation offset
    private static float GRAVITY = -95;
    private static final float JUMP_POWER = 55;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private boolean alreadyJumping = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        keyboardInput = KeyboardInput.getInstance();
        entityDescription = "The player";
    }

    public void update ( float interval, Terrain terrain){
        super.increaseRotation(0, currentTurnSpeed * interval, 0);
        float distance = currentSpeed * interval;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY() + yOffset)));
        float dz =(float) (distance * Math.cos(Math.toRadians(super.getRotY() + yOffset)));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * interval;
        super.increasePosition(0, upwardsSpeed*interval, 0);
        float terrainHeight = terrain.getTerrainHeight(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight){
            upwardsSpeed = 0;
            alreadyJumping = false;
            super.getPosition().y = terrainHeight;
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
