package inputs;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import renderEngine.DisplayManager;
import utils.Maths;

public class MousePicker {

    private Vector3f currentRay;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;
    private MouseInput mouse;
    private DisplayManager display;

    public MousePicker(Camera camera, Matrix4f projection){
        this.camera = camera;
        this.projectionMatrix = projection;
        this.viewMatrix = Maths.createViewMatrix(camera);
        mouse = MouseInput.getInstance();
        display = DisplayManager.getInstance();
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update(){
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = computeMouseRay();
    }


    public Vector3f computeMouseRay(){
        float mouseX = mouse.getX();
        float mouseY = mouse.getY();

        Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
        Vector4f eyeCoord = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoord);


        return worldRay;

    }

    private Vector3f toWorldCoords(Vector4f eyeCoords){
        Vector4f rayWorld = new Matrix4f(viewMatrix).invert().transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        return mouseRay.normalize();
    }
    private Vector4f toEyeCoords(Vector4f clipCoords){
        Matrix4f invertedProjection = new Matrix4f(projectionMatrix).invert();
        Vector4f eyeCoords = invertedProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY){
        float x = (2f * mouseX) / display.getWidth() - 1;
        float y = (2f * mouseY ) / display.getHeight() - 1;
        return new Vector2f(x, -y);
    }


}
