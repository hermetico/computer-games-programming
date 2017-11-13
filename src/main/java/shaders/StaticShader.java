package shaders;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/shaders/vertexShader.vs";
    private static final String FRAGMENT_FILE = "/shaders/fragmentShader.fs";

    public StaticShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
    @Override
    protected void bindAttributes() {
        // the positions are in the vao 0
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");

    }
}
