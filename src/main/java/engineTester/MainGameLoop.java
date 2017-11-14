package engineTester;

import entities.Entity;
import models.TexturedModel;
import org.joml.Vector3f;
import textures.ModelTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;

public class MainGameLoop implements Runnable{
    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;

    private final DisplayManager display;

    private final Thread gameLoopThread;

    private final Timer timer;

    public static void main(String[] args){
        try {
            boolean vSync = true;
            MainGameLoop gameEng = new MainGameLoop("Awesome game", 600, 480, vSync);
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

        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();



        float[] vertices = {
                -0.5f, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0

        };

        int[] indices = {
                0,1,3,
                3,1,2
        };
        float[] textureCoords = {
                0,0,
                0,1,
                1,1,
                1,0
        };

        RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("trencadis"));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Entity entity = new Entity(texturedModel, new Vector3f(0,0,0), 0,0,5,1);

        while (running && !display.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;


            input();
            renderer.prepare();
            shader.start();
            if (display.isResized()) {
                display.resize();
                display.setResized(false);
            }
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }


            renderer.render(entity,shader);
            shader.stop();
            display.updateDisplay();

            if (!display.isvSync()) {
                sync();
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

    protected void input() {
        //TODO
    }

    protected void update(float interval) {
        //TODO
    }


}