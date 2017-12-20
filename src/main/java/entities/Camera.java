package entities;

import org.joml.Vector3f;
import inputs.KeyboardInput;
import inputs.MouseInput;
import static org.lwjgl.glfw.GLFW.*;


public class Camera {
    private float distanceFromObserved = 0;
    private float angleAroundObserved = 0;


    private Vector3f position = new Vector3f(0,0,0);
    public final float DEFAULT_PITCH = 20;
    private float pitch = 0;
    private float yaw = 0;
    private float roll;
    private Entity observed;
    private MouseInput mouseInput;
    private KeyboardInput keyboardInput;

    public Camera(Entity observed){
        this.observed = observed;
        mouseInput = MouseInput.getInstance();
        keyboardInput = KeyboardInput.getInstance();

        initCameraPosition();

    }
    private void initCameraPosition(){
        angleAroundObserved = 0;
        pitch = DEFAULT_PITCH;
        distanceFromObserved = 10;
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

    public void update() {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (observed.getRotY() + angleAroundObserved);
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
        float theta = observed.getRotY() + angleAroundObserved;
        float offsetX = (float) (hDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (hDistance * Math.cos(Math.toRadians(theta)));

        position.x = observed.getPosition().x - offsetX;
        position.z = observed.getPosition().z - offsetZ;
        position.y = observed.getPosition().y + vDistance;
    }
    private float calculateHorizontalDistance(){
        return (float) (distanceFromObserved * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromObserved * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        distanceFromObserved += mouseInput.consumeZoom();
    }

    private void calculatePitch(){
        if(mouseInput.isKeyPressed(MouseInput.RIGHT_KEY)){
            pitch -=  mouseInput.getYOffset() * 0.1f;

        }
    }

    private void calculateAngleAroundPlayer(){
        if(mouseInput.isKeyPressed(MouseInput.RIGHT_KEY)){
            //angleAroundObserved += mouseInput.getXOffset() * 0.3f;
            this.observed.increaseRotation(0,mouseInput.getXOffset() * 0.3f,0);
        }
    }

}
