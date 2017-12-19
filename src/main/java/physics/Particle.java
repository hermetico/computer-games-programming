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

        Vector3f acc =  new Vector3f(acceleration).mul(delta * delta);
        position.add(acc);
        acceleration.set(0,0,0);
    }

    public void inertia(){
        Vector3f new_pos = new Vector3f(position).mul(PhysicsEngine.NEW_POS_FACTOR).sub(old_position.mul(PhysicsEngine.OLD_POS_FACTOR));
        old_position = position;
        position = new_pos;

    }

    public void rest(){
        //old_position = new Vector3f(position);
        Vector3f new_old_pos = getRealAcceleration().div(5);
        old_position = new Vector3f(position).sub(new_old_pos);
    }
    public void restY(){
        old_position.y = position.y;
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

    public void addPosition(Vector3f position){
        this.position.add(position);
    }


    public void decelerateXZ(float delta){
        float offset = 0.001f;
        Vector3f new_old_pos = getRealAcceleration();
        new_old_pos.y = 0;

        if(Math.abs(new_old_pos.x) < offset)
            new_old_pos.x = 0;

        if(Math.abs(new_old_pos.z) < offset)
            new_old_pos.z = 0;


        old_position.add(new_old_pos.mul(delta));
    }

    public Vector3f getRealAcceleration() {
        return new Vector3f(position).sub(old_position);
    }

    public Vector3f getDirection(){
        return new Vector3f(position).sub(old_position);
    }

    public void setOldPosition(Vector3f old_position) {
        this.old_position = old_position;
    }
}
