package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.joml.Vector3f;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import models.RawModel;
import utils.MouseInput;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;


public class MainGameLoop implements Runnable{
    public static final int TARGET_FPS = 60;

    public static final int TARGET_UPS = 60;

    private final DisplayManager display;

    private final Thread gameLoopThread;

    private final Timer timer;

    Loader loader;
    MasterRenderer renderer;

    Camera camera;
    MouseInput mouseInput;
    Light light;

    List<Entity> allCubes;
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
        renderer.init(display.getWidth(), display.getHeight());
        mouseInput.init(display);
        timer.init();

    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;





        RawModel  model = OBJLoader.loadObjModel("cube", loader);
        TexturedModel cubeModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("trencadis")));
        ModelTexture  texture = cubeModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(2);

        light = new Light(new Vector3f(200,200,100), new Vector3f(1,1,1));
        allCubes = new ArrayList<Entity>();
        Random random = new Random();
        for(int i = 0; i < 200; i++){
            float x = random.nextFloat() * 100 -50;
            float y = random.nextFloat() * 100 -50;
            float z = random.nextFloat() * -300;
            allCubes.add(new Entity(cubeModel, new Vector3f(x,y,z),
                    random.nextFloat() * 180f,
                    random.nextFloat() * 180f,
                    random.nextFloat() * 180f,
                    1f));
        }

        terrain = new Terrain(0,-1, loader, new ModelTexture(loader.loadTexture("grass")));
        terrain2 = new Terrain(1,-1, loader, new ModelTexture(loader.loadTexture("grass")));





        camera = new Camera();

        while (running && !display.windowShouldClose()) {


            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;


            this.input(display, camera);


            while (accumulator >= interval) {
                // update game state
                this.update(interval, allCubes);
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

        if (display.isKeyPressed(GLFW_KEY_W)) {
            camera.moveY(0.1f);
        }
        if (display.isKeyPressed(GLFW_KEY_S)) {
            camera.moveY(-0.1f);
        }
        if (display.isKeyPressed(GLFW_KEY_A)) {
            camera.moveX(-0.1f);
        }
        if (display.isKeyPressed(GLFW_KEY_D)) {
            camera.moveX(0.1f);
        }
        if(mouseInput.zoom()){
            camera.moveZ(mouseInput.consumeZoom());

        }
    }

    protected void update(float interval, List<Entity> entities) {
        for(Entity entity : entities){
            entity.increaseRotation(1,1,1);
        }

    }

    protected void render(){
        if (display.isResized()) {
            display.resize();
            renderer.updateProjectionMatrix(display.getWidth(), display.getHeight());
            display.setResized(false);
        }

        renderer.processTerrain(terrain);;
        renderer.processTerrain(terrain2);

        for(Entity entity : allCubes){
            renderer.processEntity(entity);
        }
        renderer.render(light, camera);
        display.updateDisplay();

    }


}