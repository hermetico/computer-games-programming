package engineTester;

import GUI.GUIRenderer;
import GUI.GUITexture;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import org.joml.Vector2f;
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
    GUIRenderer guiRenderer;

    Camera camera;
    MouseInput mouseInput;
    Light light;

    List<Entity> allItems;
    Terrain terrain;
    List<GUITexture> guis;
    List<Light> lights;
    Player player;
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
        mouseInput = MouseInput.getInstance();
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
        mouseInput.init(display.getWindowHandle());
        renderer.init(display.getWidth(), display.getHeight());
        timer.init();

    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;


        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("tiles"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack= new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        terrain = new Terrain(0,-1, loader, texturePack, blendMap, "heightmap");
        RawModel  model = OBJLoader.loadObjModel("grassModel", loader);
        TexturedModel grassModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("grassTexture")));
        ModelTexture  texture = grassModel.getTexture();
        texture.setShineDamper(5);
        texture.setReflectivity(1);
        texture.setHasTransparency(true);
        texture.setUseFakeLighting(true);


        ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("fernAtlas"));
        fernAtlasTexture.setNumberOfRows(2);
        RawModel fern = OBJLoader.loadObjModel("fern", loader);
        TexturedModel fernModel = new TexturedModel(fern,fernAtlasTexture);
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
            float x = random.nextFloat() * 1000;
            float z = random.nextFloat() * -1000;
            float y = terrain.getTerrainHeight(x, z);
            allItems.add(new Entity(grassModel, new Vector3f(x,y,z),
                    0, random.nextFloat() * 180f,0,1f));
        }
        for(int i = 0; i < 100; i++){
            float x = random.nextFloat() * 1000;
            float z = random.nextFloat() * -1000;
            float y = terrain.getTerrainHeight(x, z);
            allItems.add(new Entity(fernModel, new Vector3f(x,y,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f,
                    random.nextInt(4)));
        }

        for(int i = 0; i < 100; i++){
            float x = random.nextFloat() * 1000;
            float z = random.nextFloat() * -1000;
            float y = terrain.getTerrainHeight(x, z);
            allItems.add(new Entity(treeModel, new Vector3f(x,y,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f));
        }

        lights = new ArrayList<>();

        light = new Light(new Vector3f(0,10000,-7000), new Vector3f(0.4f,0.4f,0.4f));
        lights.add(light);
        lights.add(new Light(new Vector3f(100,20,-100), new Vector3f(2,0,0), new Vector3f(0.001f, 0.0001f, 0.002f)));
        lights.add(new Light(new Vector3f(200,15,-700), new Vector3f(0,2,2), new Vector3f(0.001f, 0.0001f, 0.002f)));
        lights.add(new Light(new Vector3f(600,20,-200), new Vector3f(2,2,0), new Vector3f(0.001f, 0.0001f, 0.002f)));

        guis = new ArrayList<>();
        GUITexture gui = new GUITexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        guis.add(gui);


        RawModel bunnyModel = OBJLoader.loadObjModel("bunny", loader);
        TexturedModel bunny = new TexturedModel(bunnyModel, new ModelTexture(
                loader.loadTexture("purple")));
        player = new Player(bunny, new Vector3f(0, 0, 0), 0,-45, 0,1);


        camera = new Camera(player);
        guiRenderer = new GUIRenderer(loader);

        while (running && !display.windowShouldClose()) {


            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;


            this.input();


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

        guiRenderer.cleanUp();
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

    
    public void input() {
        player.input();
        camera.input();
    }

    protected void update(float interval) {
        player.update(interval, terrain);
        camera.update(interval);

    }

    protected void render(){
        if (display.isResized()) {
            display.resize();
            renderer.updateProjectionMatrix(display.getWidth(), display.getHeight());
            display.setResized(false);
        }

        renderer.processEntity(player);
        renderer.processTerrain(terrain);

        for(Entity entity : allItems){
            renderer.processEntity(entity);
        }
        renderer.render(lights, camera);
        guiRenderer.render(guis);
        display.updateDisplay();

    }



}