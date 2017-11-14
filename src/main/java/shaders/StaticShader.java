package shaders;

import org.joml.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/shaders/vertexShader.vs";
    private static final String FRAGMENT_FILE = "/shaders/fragmentShader.fs";

    private int location_transformationMatrix;

    public StaticShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        // the positions are in the vao 0
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");

    }

    @Override
    protected void getAllUniformLocation() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix,matrix);
    }
}
