package engineTester;

import GUI.GUIRenderer;
import GUI.GUITexture;
import entities.*;
import entities.extensions.Selectable;
import inputs.KeyboardInput;
import inputs.MouseInput;
import inputs.MousePicker;
import inputs.SelectableDetector;
import models.RawEntity;
import models.TexturedModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;
import renderEngine.*;
import skybox.Skybox;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import textures.ModelTexture;
import utils.OBJC.ModelData;
import utils.OBJC.OBJFileLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class MainGameLoop implements Runnable{
    private static final int TARGET_FPS = 60;
    private static final int TARGET_UPS = 60;


    private final DisplayManager display;
    private final KeyboardInput keyboardInput;
    private final Thread gameLoopThread;

    private final Timer timer;
    private boolean debug = false;

    Loader loader;
    MasterRenderer renderer;
    GUIRenderer guiRenderer;

    Camera camera;
    MouseInput mouseInput;
    Light light;

    List<Entity> allItems;
    List<Entity> solids;
    List<Enemy> enemies;
    Terrain terrain;
    List<GUITexture> guis;
    List<Light> lights;
    Skybox skybox;
    Player player;
    MousePicker picker;
    SelectableDetector selection;
    List<Selectable> selectables;
    PhysicsEngine physics;

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
        display = DisplayManager.getInstance();
        display.setup(windowTitle, width, height, vSync);
        timer = new Timer();

        renderer = new MasterRenderer();
        loader = new Loader();
        mouseInput = MouseInput.getInstance();
        keyboardInput = KeyboardInput.getInstance();
        physics = PhysicsEngine.getInstance();

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
        skybox = new Skybox(loader);
        renderer.init(display.getWidth(), display.getHeight(), skybox);
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

        physics.init(terrain);


        // FERNS
        //ModelData dataFern = OBJFileLoader.loadOBJ("fern");
        ModelData dataFern = OBJFileLoader.loadOBJ("sphere");
        RawEntity fern = loader.loadToVAO(dataFern);

        //ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("fernAtlas"));
        //fernAtlasTexture.setNumberOfRows(2);
        ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("purple"));
        TexturedModel fernModel = new TexturedModel(fern,fernAtlasTexture);

        allItems = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < 100; i++){
            float x = random.nextFloat() * 100;
            float z = random.nextFloat() * -100;
            float y = terrain.getTerrainHeight(x, z) + random.nextFloat() * 100;

            Entity n = new Entity(fernModel, new Vector3f(x,y,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f,
                    random.nextInt(4));
            n.setEntityDescription("fern " + i);
            allItems.add(n);
        }

	    // enemies
        enemies = new ArrayList<Enemy>();
        /*ModelData dataSheep = OBJFileLoader.loadOBJ("cube");
        RawEntity sheep = loader.loadToVAO(dataSheep);

        ModelTexture sheepTexture = new ModelTexture(loader.loadTexture("trencadis"));
        TexturedModel sheepModel = new TexturedModel(sheep,sheepTexture);
        for(int i = 0; i < 15; i++) {

            float x = random.nextFloat() * 1000;
            float z = random.nextFloat() * -1000;
            float y = terrain.getTerrainHeight(x, z);
            Enemy s = new Enemy(sheepModel, new Vector3f(x,y,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f);

            s.setEntityDescription("Enemy " + i);
            enemies.add(s);
        }
        */
        lights = new ArrayList<>();

        light = new Light(new Vector3f(0,10000,-7000), new Vector3f(0.4f,0.4f,0.4f));
        lights.add(light);
        lights.add(new Light(new Vector3f(100,20,-100), new Vector3f(2,0,0), new Vector3f(0.001f, 0.0001f, 0.002f)));
        lights.add(new Light(new Vector3f(200,15,-700), new Vector3f(0,2,2), new Vector3f(0.001f, 0.0001f, 0.002f)));
        lights.add(new Light(new Vector3f(600,20,-200), new Vector3f(2,2,0), new Vector3f(0.001f, 0.0001f, 0.002f)));

        guis = new ArrayList<>();
        GUITexture gui = new GUITexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        guis.add(gui);


        ModelData bunnyData = OBJFileLoader.loadOBJ("cube");
        RawEntity bunnyEntity = loader.loadToVAO(bunnyData);
        TexturedModel bunny = new TexturedModel(bunnyEntity, new ModelTexture(
                loader.loadTexture("purple")));
        player = new Player(bunny, new Vector3f(0, 0, 0), 0,90, 0,1f);


        //camera = new Camera(enemies.get(0));
        camera = new Camera(player);
        guiRenderer = new GUIRenderer(loader);
        picker = new MousePicker(camera, renderer.getProjectionMatrix());

        selection = new SelectableDetector();
        selectables = new ArrayList<>();
        solids = new ArrayList<>();
        selectables.add(player);

        for(Entity entity : allItems){
            selectables.add(entity);
            physics.getRigidBodies().add(new RigidBody(PhysicsEngine.OBJECT_SPHERE, entity));
            solids.add(entity);
        }

        for(Entity entity : enemies){
            selectables.add(entity);
            solids.add(entity);
        }



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
        if(keyboardInput.isKeyPressed(KeyboardInput.BOXES_KEY)){
            debug = !debug;

            for(Selectable selected: selectables){
                selected.setDebugSelected(debug);
            }

        }

        if(mouseInput.isKeyPressed(MouseInput.LEFT_KEY)){
            Vector3f ray = picker.computeMouseRay();
            selection.selectGameItem(selectables, camera, ray);
        }

    }

    protected void update(float interval) {
        //for(Enemy entity : enemies) {
        //    entity.update(interval, terrain, player.getPosition());
        //}
        player.update(interval, terrain, solids);
        camera.update(interval);
        skybox.update(interval);
        picker.update();

        physics.update(interval);

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
        for(Enemy entity : enemies){
            renderer.processEntity(entity);
        }

        renderer.render(lights, camera);
        renderer.renderBoxes(selectables, camera);
        //guiRenderer.render(guis);
        display.updateDisplay();

    }



}
