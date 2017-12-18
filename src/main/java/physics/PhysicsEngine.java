package physics;

import org.joml.Vector3f;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    public static float NEW_POS_FACTOR = 1.99f;
    public static float OLD_POS_FACTOR = 0.99f;
    public static int OBJECT_SPHERE = 1;
    public static int OBJECT_CUBE = 2;
    public static int STEPS = 1;

    public static Vector3f GRAVITY = new Vector3f(0f, -9.1f, 0f);
    private Terrain terrain;
    private List<RigidBody> rigidBodies = new ArrayList<RigidBody>();

    private static PhysicsEngine instance;

    private PhysicsEngine(){}

    public void init(Terrain terrain){
        this.terrain = terrain;
    }

    public void update(float delta){

        delta /= STEPS;

        for(int i = 0; i < STEPS; i++){
            applyGravity();
            applyAcceleration(delta);
            checkWorldConstraints();
            //internal_constraints
            applyInertia();

            // collide with others

        }

        updateBodies();
    }

    private void applyGravity(){
        for(RigidBody body: rigidBodies){
            body.increaseAcceleration(GRAVITY);
        }
    }

    private void applyAcceleration( float delta){
        for(RigidBody body: rigidBodies){
            body.applyAcceleration(delta);
        }
    }


    private void checkWorldConstraints(){
        for(RigidBody body: rigidBodies){
            //TODO SPHERES SO FAR
            float radius = body.getRadius();
            Vector3f bodyPosition = body.getPosition();
            float floor = terrain.getTerrainHeight(bodyPosition);


            if(bodyPosition.y - radius < floor + radius) {
                body.setPosition(new Vector3f(bodyPosition.x, floor + radius, bodyPosition.z));
            }
        }
    }
    private void applyInertia(){
        for(RigidBody body: rigidBodies){
            body.applyInertia();
        }
    }

    private void updateBodies(){
        for(RigidBody body: rigidBodies){
            body.update();
        }
    }

    public static PhysicsEngine getInstance(){
        if(instance == null){
            instance = new PhysicsEngine();
        }
        return instance;
    }

    public List<RigidBody> getRigidBodies() {
        return rigidBodies;
    }
}
