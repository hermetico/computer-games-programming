package physics;


import org.joml.Vector3f;

public class Particle {

    private Vector3f position;
    private Vector3f old_position;
    private Vector3f acceleration;

    private RigidBody body;

    public Particle( RigidBody body){
        this(body, new Vector3f(0f,0f,0f));

    }

    public Particle( RigidBody body, Vector3f position){
        this.position = new Vector3f(position);
        old_position = new Vector3f(position);
        acceleration = new Vector3f(0f,0f,0f);
        this.body = body;
    }

    public void accelerate(float delta){
        position.add(acceleration.mul(delta * delta));
        acceleration.set(0,0,0);
    }

    public void inertia(){
        Vector3f new_pos = new Vector3f(position).mul(PhysicsEngine.NEW_POS_FACTOR).sub(old_position.mul(PhysicsEngine.OLD_POS_FACTOR));
        //Vector3f new_pos = new Vector3f(position).mul(2).sub(old_position);
        old_position = position;
        position = new_pos;
    }

    public void rest(){
        acceleration.set(0f);
        old_position = new Vector3f(position);
    }

    public void increaseAcceleration(Vector3f acceleration){
        this.acceleration.add(acceleration);
    }

    public void setPosition(Vector3f position){
        this.position = position;
    }

    public Vector3f getPosition(){
        return new Vector3f(position);
    }

}
