package shaders;

import entities.Camera;
import org.joml.Matrix4f;
import utils.Maths;

public class BoundingBoxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/shaders/boundingBoxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "/shaders/boundingBoxFragmentShader.glsl";
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    public BoundingBoxShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);

    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix,matrix);

    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }
}
