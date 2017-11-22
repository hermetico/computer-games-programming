package entities;

import org.joml.Vector3f;
import utils.KeyboardInput;
import utils.MouseInput;
import static org.lwjgl.glfw.GLFW.*;


public class Camera {
    private float distanceFromPlayer = 0;
    private float angleAroundPlayer = 0;


    private Vector3f position = new Vector3f(0,0,0);
    private float pitch = 0;
    private float yaw = 0;
    private float roll;
    private Player player;
    private MouseInput mouseInput;
    private KeyboardInput keyboardInput;

    public Camera(Player player){
        this.player = player;
        mouseInput = MouseInput.getInstance();
        keyboardInput = KeyboardInput.getInstance();

        initCameraPosition();

    }
    private void initCameraPosition(){
        angleAroundPlayer = player.getyOffset();
        pitch = 15;
        distanceFromPlayer = 60;
        yaw = 0;
    }

    public void input(){
        if(keyboardInput.isKeyPressed(GLFW_KEY_R)){
            initCameraPosition();
        }
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
    }

    public void update(float interval) {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }


    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float hDistance, float vDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (hDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (hDistance * Math.cos(Math.toRadians(theta)));

        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + vDistance;
    }
    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        distanceFromPlayer += mouseInput.consumeZoom();
    }

    private void calculatePitch(){
        if(mouseInput.isKeyPressed(GLFW_MOUSE_BUTTON_LEFT)){
            pitch -=  mouseInput.getYOffset() * 0.1f;

        }
    }

    private void calculateAngleAroundPlayer(){
        if(mouseInput.isKeyPressed(GLFW_MOUSE_BUTTON_LEFT)){
            angleAroundPlayer += mouseInput.getXOffset() * 0.3f;
        }
    }

}
