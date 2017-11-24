package inputs;

import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;


public class MouseInput {
    private static MouseInput instance;

    private  Vector2f mousePos = new Vector2f(0, 0);
    private  Vector2f mouseOffset = new Vector2f(0,0);
    private final float ZOOM_DIRECTION = -1;
    private float zoomOffset = 0;
    private long windowHandle;

    public static final int RIGHT_KEY = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int LEFT_KEY = GLFW_MOUSE_BUTTON_LEFT;
    private MouseInput() {}

    public static MouseInput getInstance(){
        if(instance == null){
            instance = new MouseInput();
        }
        return instance;
    }

    public void init(long handler) {
        this.windowHandle = handler;

        glfwSetScrollCallback(handler, (windowHandle, xoffset, yoffset) -> {
            zoomOffset = (float) yoffset * ZOOM_DIRECTION;
        });

        glfwSetCursorPosCallback(handler, (windowHandle, xPos, yPos)->{
            this.mouseOffset.x = this.mousePos.x - (float) xPos;
            this.mouseOffset.y = this.mousePos.y - (float) yPos;
            this.mousePos.x = (float) xPos;
            this.mousePos.y = (float) yPos;
        });

    }

    public boolean zoom() {
        return zoomOffset != 0.0;
    }

    public float consumeZoom(){
        float offset = zoomOffset;
        zoomOffset = 0;
        return offset;
    }

    public float getYOffset(){return mouseOffset.y;}
    public float getXOffset(){return mouseOffset.x;}
    public float getX(){ return mousePos.x;}
    public float getY(){ return mousePos.y;}


    public boolean isKeyPressed(int keyCode) {
        return glfwGetMouseButton(this.windowHandle, keyCode) == GLFW_PRESS;
    }
}

