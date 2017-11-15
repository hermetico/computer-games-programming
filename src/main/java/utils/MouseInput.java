package utils;

import org.joml.Vector2d;
import org.joml.Vector2f;
import renderEngine.DisplayManager;
import static org.lwjgl.glfw.GLFW.*;


public class MouseInput {

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;
    private float zoomOffset = 0;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(DisplayManager display) {
        glfwSetScrollCallback(display.getWindowHandle(), (windowHandle, xoffset, yoffset) -> {
            zoomOffset = (float) yoffset * -1;
        });
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input(DisplayManager display) {
        //TODO
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

