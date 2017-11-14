package engineTester;

import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import org.joml.Vector3f;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;
import utils.MouseInput;


import static org.lwjgl.glfw.GLFW.*;


public class MainGameLoop implements Runnable{
    public static final int TARGET_FPS = 60;

    public static final int TARGET_UPS = 60;

    private final DisplayManager display;

    private final Thread gameLoopThread;

    private final Timer timer;

    Loader loader;
    Renderer renderer;
    StaticShader shader;
    Entity entity;
    Camera camera;
    MouseInput mouseInput;

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

        timer.init();

    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;

        loader = new Loader();
        mouseInput = new MouseInput();
        mouseInput.init(display);
        shader = new StaticShader();
        renderer = new Renderer(shader, display.getWidth(), display.getHeight());


        RawModel  model = OBJLoader.loadObjModel("stall", loader);
        //RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedModel = new TexturedModel(model, texture);

        entity = new Entity(texturedModel, new Vector3f(0,0,-50), 0,0,0,1);
        camera = new Camera();

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
        shader.cleanUp();
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

    protected void update(float interval) {
        entity.increaseRotation(0,1,0);
    }

    protected void render(){
        if (display.isResized()) {
            display.resize();
            display.setResized(false);
        }

        renderer.prepare();
        shader.start();
        shader.loadViewMatrix(camera);
        renderer.render(entity,shader);

        shader.stop();
        display.updateDisplay();

    }


}