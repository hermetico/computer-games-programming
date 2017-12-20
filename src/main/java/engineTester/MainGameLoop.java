package engineTester;

import Factories.Factory;
import GUI.GUIRenderer;
import GUI.GUITexture;
import entities.*;
import entities.extensions.Selectable;
import inputs.KeyboardInput;
import inputs.MouseInput;
import inputs.MousePicker;
import inputs.SelectableDetector;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;
import renderEngine.*;
import skybox.Skybox;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;


public class MainGameLoop implements Runnable{
    private static final int TARGET_FPS = 30;
    private static final int TARGET_UPS = 30;


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
    List<Cloud> enemies;
    Terrain terrain;
    List<GUITexture> guis;
    List<Light> lights;
    Skybox skybox;
    Player player;
    MousePicker picker;
    SelectableDetector selection;
    List<Selectable> selectables;
    PhysicsEngine physics;
    Factory factory;
    List<RigidBody> cubes;
    List<Entity> visible;

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
        factory = Factory.getInstance();



    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float shoots = 1f / (TARGET_UPS/10);
        boolean running = true;

        allItems = new ArrayList<>();
        enemies = new ArrayList<Cloud>();
        cubes = new ArrayList<RigidBody>();
        visible = new ArrayList<Entity>();
        selectables = new ArrayList<>();
        solids = new ArrayList<>();
        factory.init( cubes, visible, selectables);
        terrain = factory.createTerrain();

        physics.init(terrain);


        // CUBES

        factory.createCube();

        lights = factory.createGameLights();

        guis = new ArrayList<>();


        player = factory.createPlayer();
        lights.add(player.getJetLight());

        //camera = new Camera(enemies.get(0));
        camera = new Camera(player);
        guiRenderer = new GUIRenderer(loader);

        picker = new MousePicker(camera, renderer.getProjectionMatrix());

        selection = new SelectableDetector();

        timer.init();

        float shootInterval = 0;
        while (running && !display.windowShouldClose()) {



            elapsedTime = timer.getElapsedTime();
            shootInterval += elapsedTime;

            if( shootInterval > shoots){
                shootInterval = 0;
                player.shooting_allowed();
            }

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
        physics.input();
        camera.input();
        player.input();

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

        player.update();
        camera.update(interval);
        skybox.update(interval);
        picker.update();
        for(RigidBody c : physics.getCubes()){

            ((Cloud)c.getEntity()).update(interval);
        }
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

        for(Entity entity : visible){
            renderer.processEntity(entity);
        }

        renderer.render(lights, camera);
        renderer.renderBoxes(selectables, camera);
        display.updateDisplay();

    }



}
