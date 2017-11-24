package inputs;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardInput {
    private static KeyboardInput instance;
    private long windowHandle;

    private KeyboardInput(){}

    public void init(long windowHandle){
        this.windowHandle = windowHandle;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public static KeyboardInput getInstance(){
        if(instance == null){
            instance = new KeyboardInput();
        }
        return instance;
    }
}
