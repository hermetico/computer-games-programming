package shaders;

import entities.Camera;
import entities.Light;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.Maths;

import java.util.List;

public class StaticShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 4;
    private static final String VERTEX_FILE = "/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "/shaders/fragmentShader.glsl";




    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColour;
    private int[] location_lightAttenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColour;
    private int location_numberOfRows;
    private int location_offset;

    public StaticShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        // the positions are in the vao 0
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");

    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");

        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColour = super.getUniformLocation("skyColour");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_lightAttenuation = new int[MAX_LIGHTS];

        for(int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_lightAttenuation[i] = super.getUniformLocation("lightAttenuation[" + i + "]");
        }
    }

    public void loadSkyColour(float r, float g, float b){
        super.loadVector(location_skyColour, new Vector3f(r,g,b));
    }

    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(location_useFakeLighting, useFake);
    }

    public void loadShineVariables(float shineDamper, float reflectivity){
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix,matrix);

    }

    public void loadLights(List<Light> lights){
        for(int i=0; i < MAX_LIGHTS; i++) {
            if( i < lights.size()) {
                super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
                super.loadVector(location_lightColour[i], lights.get(i).getColor());
                super.loadVector(location_lightAttenuation[i], lights.get(i).getAttenuation());
            }else{
                super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
                super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
                super.loadVector(location_lightAttenuation[i], new Vector3f(1,0,0));
            }
        }
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);

    }

    public void loadNumberOfRows(int numberOfRows){
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y){
        super.load2DVector(location_offset, new Vector2f(x, y));
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }
}
