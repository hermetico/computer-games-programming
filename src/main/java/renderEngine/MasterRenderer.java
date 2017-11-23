package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.Skybox;
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
    private static final float FOV = 70; // field of view
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;
    private static final float RED = 0f;
    private static final float GREEN = 0f;
    private static final float BLUE = 0f;

    private Matrix4f projectionMatrix;
    private StaticShader entityShader;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private Skybox skybox;
    private TerrainShader terrainShader;
    private Map<TexturedModel, List<Entity>> entities;
    private List<Terrain> terrains;


    public void init(float displayWidth, float displayHeight, Skybox skybox){
        entityShader = new StaticShader();
        entityRenderer = new EntityRenderer(entityShader);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader);
        this.skybox = skybox;
        entities = new HashMap<>();
        terrains = new ArrayList<>();

        updateProjectionMatrix(displayWidth, displayHeight);


    }

    public void updateProjectionMatrix(float displayWidth, float displayHeight){
        createProjectionMatrix(displayWidth, displayHeight);
        entityRenderer.loadProjectionMatrix(this.projectionMatrix);
        terrainRenderer.loadProjectionMatrix(this.projectionMatrix);
        skybox.getRenderer().loadProjectionMatrix(this.projectionMatrix);

    }


    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera){
        prepare();

        entityShader.start();
        entityShader.loadSkyColour(RED, GREEN, BLUE);
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();

        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        skybox.render(camera, RED, GREEN, BLUE);

        terrains.clear();
        entities.clear();

    }

    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    public void prepare(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED,GREEN,BLUE,1);
    }

    private void createProjectionMatrix(float width, float height){
        float aspectRatio = width / height;
        projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        projectionMatrix.perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    public void cleanUp(){
        entityShader.cleanUp();
        terrainShader.cleanUp();
    }
}
