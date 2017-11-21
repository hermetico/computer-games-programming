package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import models.RawModel;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import utils.KeyboardInput;
import utils.MouseInput;
import utils.OBJConverter.ModelData;
import utils.OBJConverter.OBJFileLoader;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;


public class MainGameLoop implements Runnable{
    private static final int TARGET_FPS = 60;
    private static final int TARGET_UPS = 60;


    private final DisplayManager display;
    private final KeyboardInput keyboardInput;
    private final Thread gameLoopThread;

    private final Timer timer;

    Loader loader;
    MasterRenderer renderer;

    Camera camera;
    MouseInput mouseInput;
    Light light;

    List<Entity> allItems;
    Terrain terrain;
    Terrain terrain2;
    public static void main(String[] args){
        try {
            boolean vSync = false;
            MainGameLoop gameEng = new MainGameLoop("Awesome game", 1280, 720, vSync);
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }

    public MainGameLoop(String windowTitle, int width, int height, boolean vSync) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        display = new DisplayManager(windowTitle, width, height, vSync);
        timer = new Timer();
        renderer = new MasterRenderer();
        loader = new Loader();
        mouseInput = new MouseInput();
        keyboardInput = KeyboardInput.getInstance();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    protected void init() throws Exception {



        display.createDisplay();
        keyboardInput.init(display.getWindowHandle());
        renderer.init(display.getWidth(), display.getHeight());
        mouseInput.init(display);
        timer.init();

    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;





        RawModel  model = OBJLoader.loadObjModel("grassModel", loader);
        TexturedModel grassModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("grassTexture")));
        ModelTexture  texture = grassModel.getTexture();
        texture.setShineDamper(5);
        texture.setReflectivity(1);
        texture.setHasTransparency(true);
        texture.setUseFakeLighting(true);


        RawModel fern = OBJLoader.loadObjModel("fern", loader);
        TexturedModel fernModel = new TexturedModel(fern, new ModelTexture(loader.loadTexture("fern")));
        texture = fernModel.getTexture();
        texture.setShineDamper(5);
        texture.setReflectivity(1);
        texture.setHasTransparency(true);
        texture.setUseFakeLighting(true);

        ModelData dataTree = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel tree = loader.loadToVAO(dataTree.getVertices(), dataTree.getTextureCoords(),
                dataTree.getNormals(), dataTree.getIndices());
        TexturedModel treeModel = new TexturedModel(tree, new ModelTexture(loader.loadTexture("lowPolyTree")));
        texture = treeModel.getTexture();
        texture.setShineDamper(100);
        texture.setReflectivity(2);

        allItems = new ArrayList<Entity>();
        Random random = new Random();
        for(int i = 0; i < 50; i++){
            float x = random.nextFloat() * 100 -50;
            float z = random.nextFloat() * -300;
            allItems.add(new Entity(grassModel, new Vector3f(x,0,z),
                    0, random.nextFloat() * 180f,0,1f));
        }
        for(int i = 0; i < 100; i++){
            float x = random.nextFloat() * 500 - 250;
            float z = random.nextFloat() * -400;
            allItems.add(new Entity(fernModel, new Vector3f(x,0,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f));
        }

        for(int i = 0; i < 100; i++){
            float x = random.nextFloat() * 500 - 250;
            float z = random.nextFloat() * -400;
            allItems.add(new Entity(treeModel, new Vector3f(x,0,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f));
        }


        light = new Light(new Vector3f(200,200,100), new Vector3f(1,1,1));
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("tiles"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack= new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        terrain = new Terrain(0,-1, loader, texturePack, blendMap);
        terrain2 = new Terrain(-1,-1, loader, texturePack, blendMap);





        camera = new Camera();
        camera.moveY(1);

        while (running && !display.windowShouldClose()) {


            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;


            this.input(display, camera);


            while (accumulator >= interval) {
                // update game state
                this.update(interval);
                accumulator -= interval;
            }


            this.render();

            if (!display.isvSync()) {
                this.sync();
            }
        }

        renderer.cleanUp();
        loader.cleanUp();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    
    public void input(DisplayManager display, Camera camera) {

        if (keyboardInput.isKeyPressed(GLFW_KEY_W)) {
            camera.moveY(0.1f);
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_S)) {
            camera.moveY(-0.1f);
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_A)) {
            camera.moveX(-0.1f);
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_D)) {
            camera.moveX(0.1f);
        }
        if(mouseInput.zoom()){
            camera.moveZ(mouseInput.consumeZoom());

        }
    }

    protected void update(float interval) {
        //for(Entity entity : items){
        //    entity.increaseRotation(1,1,1);
        //}

    }

    protected void render(){
        if (display.isResized()) {
            display.resize();
            renderer.updateProjectionMatrix(display.getWidth(), display.getHeight());
            display.setResized(false);
        }

        renderer.processTerrain(terrain);;
        renderer.processTerrain(terrain2);

        for(Entity entity : allItems){
            renderer.processEntity(entity);
        }
        renderer.render(light, camera);
        display.updateDisplay();

    }



}