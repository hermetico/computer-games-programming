package skybox;

import entities.Camera;
import org.joml.Matrix4f;
import renderEngine.Loader;
import utils.Maths;

public class Skybox {
    private SkyboxRenderer renderer;
    private SkyboxShader shader;
    private static final float ROTATION_SPEED_X = 0.3f;
    private static final float ROTATION_SPEED_Y = 0.7f;
    private float rotationY = 0;
    private float rotationX = 0;

    public Skybox(Loader loader) {
        shader = new SkyboxShader();
        renderer = new SkyboxRenderer(loader, shader);
    }

    public void update(float interval){
        rotationY += ROTATION_SPEED_Y * interval;
        //rotationX += ROTATION_SPEED_X * interval;
    }


    public void render(Camera camera, float r, float g, float b){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        matrix = matrix.rotateY((float) Math.toRadians(rotationY))
                .rotateX((float) Math.toRadians(rotationX));
        renderer.render(matrix, r, g, b);
    }

    public SkyboxRenderer getRenderer() {
        return renderer;
    }
}
