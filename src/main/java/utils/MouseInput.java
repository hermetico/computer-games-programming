package utils;

import org.joml.Vector2d;
import org.joml.Vector2f;
import renderEngine.DisplayManager;
import static org.lwjgl.glfw.GLFW.*;


public class MouseInput {

    private final Vector2d previousPos = new Vector2d(-1, -1);;

    private final Vector2d currentPos = new Vector2d(0, 0);

    private final Vector2f displVec = new Vector2f();

    private float zoomOffset = 0;
    private static MouseInput instance;

    private MouseInput() {}
    public static MouseInput getInstance(){
        if(instance == null){
            instance = new MouseInput();
        }
        return instance;
    }

    public void init(DisplayManager display) {
        glfwSetScrollCallback(display.getWindowHandle(), (windowHandle, xoffset, yoffset) -> {
            zoomOffset = (float) yoffset * -1;
        });
    }

    public Vector2f getDisplVec() {
        return displVec;
    }


    public boolean zoom() {
        return zoomOffset != 0.0;
    }

    public float consumeZoom(){
        float offset = zoomOffset;
        zoomOffset = 0;
        return offset;
    }
}

