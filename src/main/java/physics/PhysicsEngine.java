package physics;

import audio.AudioMaster;
import audio.Source;
import entities.Entity;
import Factories.Factory;
import inputs.KeyboardInput;
import org.joml.Vector3f;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class PhysicsEngine {
    private KeyboardInput keyboardInput;
    private RigidBody player;
    private boolean player_jumping = true;
    private boolean set_jump;

    public static float NEW_POS_FACTOR = 1.99f;
    public static float OLD_POS_FACTOR = 0.99f;

    public static int OBJECT_SPHERE = 1;
    public static int OBJECT_CUBE = 2;
    public static int STEPS = 2;

    private final Vector3f GRAVITY = new Vector3f(0f, -18f, 0f);
    private final float JUMP_POWER = 0.5f;
    private final float PLAYER_MOVE_SPEED = 0.5f;
    private final float PLAYER_TURN_SPEED = 50;


    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;


    private Terrain terrain;
    private List<RigidBody> cubes = new ArrayList<RigidBody>();
    private List<RigidBody> bullets = new ArrayList<RigidBody>();

    private static PhysicsEngine instance;
    private Factory factory;

    private float currentMaxHeight = 0.0f;
    private int shootedCubes = 0;
    private boolean hideBelowMaxHeight = false;
    private boolean restoreCubes = false;

    AudioMaster var;


    private PhysicsEngine() {}

    public void init(Terrain terrain){
        var = new AudioMaster();
        var.init(1);
        this.terrain = terrain;
        this.keyboardInput = KeyboardInput.getInstance();
        factory = Factory.getInstance();
    }

    public void update(float delta){

        delta /= STEPS;
        Entity theplayer = player.getEntity();
        for(int i = 0; i < STEPS; i++){

            checkCubeColisions();
            checkBulletCollisions();

            applyGravity();
            applyAcceleration(delta);

            checkWorldConstraints();
            checkPlayerWorldConstraints();

            //internal_constraints

            applyInertia();

            checkCollisions();


            updateBodies();

            // player movement
            if(currentSpeed != 0) {
                float distance = currentSpeed * delta;

                float dx = (float) (distance * Math.sin(Math.toRadians(theplayer.getRotY())));
                float dz = (float) (distance * Math.cos(Math.toRadians(theplayer.getRotY())));
                player.addPosition(new Vector3f(dx, 0, dz));

            }else{

                player.decelerateXZ(delta);

            }

            if(currentTurnSpeed != 0.0) {

                if (player_jumping) {
                    Vector3f acceleration = player.getAcceleration();
                    float acc = (float) Math.sqrt((float) (Math.pow(acceleration.x, 2) + Math.pow(acceleration.y, 2)+ Math.pow(acceleration.z, 2)));
                    float distance = acc * PLAYER_MOVE_SPEED * delta;
                    float dx = (float) (distance * Math.sin(Math.toRadians(theplayer.getRotY())));
                    float dz = (float) (distance * Math.cos(Math.toRadians(theplayer.getRotY())));
                    player.addPosition(new Vector3f(dx, 0, dz));
                }

                theplayer.increaseRotation(0f, currentTurnSpeed * delta, 0f);
            }
        }

        if( restoreCubes ){
            this.soundCloud();
            factory.restoreAllCubes();
            restoreCubes = false;
        }

        if( hideBelowMaxHeight ){
            factory.hideCubesBelow(currentMaxHeight);
            hideBelowMaxHeight = false;
        }

    }

    private void applyGravity(){
        for(RigidBody body: bullets){
            body.increaseAcceleration(GRAVITY);
        }

        player.increaseAcceleration(GRAVITY);
    }

    private void applyAcceleration( float delta){

        for(RigidBody body: bullets){
            body.applyAcceleration(delta);

        }
        player.applyAcceleration(delta);
    }


    private void checkCubeColisions() {

        AABB pBox = player.getEntity().getAABB();
        for (RigidBody body : cubes) {
            if (body.equals(player)) continue;

            if (pBox.colliding(body.getEntity().getAABB())) {
                // going down?
                if (player.getYDirection() == -1) {
                    if(pBox.getMin().y < body.getEntity().getAABB().getMax().y) {
                        float diff =  body.getEntity().getAABB().getMax().y  - pBox.getMin().y;
                        player.addPosition(new Vector3f(0, diff , 0));
                        player.rest();
                        player_jumping = false;
                        if( body.getEntity().getAABB().getMax().y > currentMaxHeight){
                            hideBelowMaxHeight = true;
                            currentMaxHeight = body.getEntity().getAABB().getMax().y;
                            this.soundCloud();
                        }
                    }


                }

            }

        }
    }

    private void checkPlayerWorldConstraints(){
        //TODO SPHERES SO FAR
        float radius = player.getRadius();
        Vector3f playerPosition = player.getPosition();
        float floor = terrain.getTerrainHeight(playerPosition);

        float tx, ty, tz;
        tx =  Math.min(terrain.X_MAX - radius, Math.max(playerPosition.x , terrain.X_MIN + radius));
        tz =  Math.min(terrain.Z_MAX - radius, Math.max(playerPosition.z , terrain.Z_MIN + radius));


        if(playerPosition.y < floor + radius){
            ty =(floor + radius);


                player_jumping = false;
                if(currentMaxHeight > 0) {
                    restoreCubes = true;
                }
            currentMaxHeight = 0;
            player.setPosition(new Vector3f(tx, ty, tz));
        }
    }
    private void checkWorldConstraints(){
        for (Iterator<RigidBody> iterator = bullets.iterator(); iterator.hasNext();) {
            RigidBody body = iterator.next();
            //TODO SPHERES SO FAR
            float radius = body.getRadius();
            Vector3f bodyPosition = body.getPosition();
            float floor = terrain.getTerrainHeight(bodyPosition);

            float tx, ty, tz;
            tx =  Math.min(terrain.X_MAX - radius, Math.max(bodyPosition.x , terrain.X_MIN + radius));
            tz =  Math.min(terrain.Z_MAX - radius, Math.max(bodyPosition.z , terrain.Z_MIN + radius));
            ty =  Math.max(bodyPosition.y, floor + radius);

            if(bodyPosition.y < floor + radius){

                iterator.remove();
                removeVisible(body.getEntity());
            }
        }

    }

    private void checkBulletCollisions(){
        for (Iterator<RigidBody> iterator = bullets.iterator(); iterator.hasNext();) {
            RigidBody a = iterator.next();
            AABB aBox = a.getEntity().getAABB();
            for (Iterator<RigidBody> iteratorb = cubes.iterator(); iteratorb.hasNext();) {
                RigidBody b = iteratorb.next();
                if (aBox.colliding(b.getEntity().getAABB())) {
                    // going down?
                    removeVisible(a.getEntity());
                    removeVisible(b.getEntity());
                    iteratorb.remove();
                    iterator.remove();
                    shootedCube(b);
                }
            }
        }
    }
    private void checkCollisions(){
        for(RigidBody a : bullets ){
            for(RigidBody b : bullets ) {
                if (a.equals(b)) continue;
                float x = a.getPosition().x - b.getPosition().x;
                float y = a.getPosition().y - b.getPosition().y;
                float z = a.getPosition().z - b.getPosition().z;

                float length = (float)Math.sqrt(x * x + y * y + z * z);
                float target = a.getRadius() + b.getRadius();

                if (length < target){
                    float factor = (length - target) / length;
                    float scale_a, scale_b, factor_a, factor_b;
                    scale_a = a.getMass() / a.getMass() * b.getMass();
                    scale_b = b.getMass() / a.getMass() * b.getMass();

                    factor_a = scale_a / scale_a + scale_b;
                    factor_b = scale_b  / scale_a + scale_b;

                    Vector3f aPos = new Vector3f( -x  * factor * factor_a, -y * factor * factor_a, -z * factor * factor_a);
                    Vector3f bPos = new Vector3f( x  * factor * factor_b, y * factor * factor_b, z * factor * factor_b);

                    a.addPosition(aPos);
                    b.addPosition(bPos);
                }

            }
        }
    }
    private void applyInertia(){
        for(RigidBody body: bullets){
            body.applyInertia();
        }
        player.applyInertia();
    }

    private void updateBodies(){
        for(RigidBody body: bullets){
            body.update();
        }
        player.update();
    }

    public static PhysicsEngine getInstance(){
        if(instance == null){
            instance = new PhysicsEngine();
        }
        return instance;
    }


    public List<RigidBody> getBullets() {
        return bullets;
    }

    public List<RigidBody> getCubes() {
        return cubes;
    }

    public  void input(){

        if (keyboardInput.isKeyPressed(GLFW_KEY_W)) {
            this.currentSpeed = PLAYER_MOVE_SPEED;
        }else if (keyboardInput.isKeyPressed(GLFW_KEY_S)) {
            this.currentSpeed = -PLAYER_MOVE_SPEED;
        }else{
            this.currentSpeed = 0f;
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_D)) {
            this.currentTurnSpeed = -PLAYER_TURN_SPEED;
        }else if (keyboardInput.isKeyPressed(GLFW_KEY_A)) {
            this.currentTurnSpeed = PLAYER_TURN_SPEED;
        }else{
            this.currentTurnSpeed = 0;
        }

        if(keyboardInput.isKeyPressed(GLFW_KEY_SPACE)){
            this.soundJump(player.getPosition());
            if(!player_jumping) {
                player_jumping = true;
                player.addPosition(new Vector3f(0, JUMP_POWER, 0));
            }
        }

    }

    public void setPlayer(RigidBody player){
        this.player = player;
    }

    public float getCurrentMaxHeight() {

        return currentMaxHeight;
    }

    public int getShootedCubes() {
        return shootedCubes;
    }

    private void removeVisible(Entity entity){
        factory.removeVisible(entity);

    }

    private void shootedCube(RigidBody cube){
        this.soundDesolve();
        shootedCubes++;
        factory.removeCube(cube);
    }

    public void soundJump(Vector3f sourceIN){
        final int buffer = var.loadSound("audio/bounce2.wav");
        final Source source = new Source();
        source.setVolume(1550.0f);
        source.setPosition(sourceIN.x, sourceIN.y, sourceIN.z);
        this.var.setListenerData(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        source.setLooping(false);

        if(!source.isPlaying()){
            source.play(buffer);
        }
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
    }

    public void soundShoot(){
        final int buffer = var.loadSound("audio/shoot.wav");
        final Source source = new Source();
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        this.var.setListenerData(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        source.setLooping(false);
        source.play(buffer);
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
    }

    public void soundDesolve(){
        final int buffer = var.loadSound("audio/desolve.wav");
        final Source source = new Source();
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        this.var.setListenerData(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        source.setLooping(false);
        source.setVolume(0.7f);
        source.play(buffer);
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
    }


    public void soundCloud(){
        final int buffer = var.loadSound("audio/cloud.wav");
        final Source source = new Source();
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        this.var.setListenerData(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        source.setLooping(false);
        source.play(buffer);
        source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
    }
    public void cleanUpSounds(){
        var.cleanUp();
    }

}
