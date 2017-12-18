package physics;

import entities.Entity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RigidBody {

    private int bodyType;
    private float mass;
    private Entity entity;
    private List<Particle> particles;

    public RigidBody(int bodyType, Entity entity, float mass){
        this.bodyType = bodyType;
        this.entity = entity;
        this.mass = mass;
        setupBody();

    }

    private void setupBody(){
        particles = new ArrayList<>();
        //TODO SPHERES SO FAR
        particles.add(new Particle(this, entity.getPosition()));
    }

    public void increaseAcceleration(Vector3f acceleration){
        for(Particle particle: particles){
            particle.increaseAcceleration(acceleration);
        }
    }

    public void applyAcceleration(float delta){
        for(Particle particle: particles){
            particle.accelerate(delta);
        }
    }

    public void applyInertia(){
        for(Particle particle: particles){
            particle.inertia();
        }
    }

    public void update(){
        //TODO SPHERES SO FAR
        this.entity.updatePosition(particles.get(0).getPosition());
    }

    public Vector3f getPosition(){
        //TODO SPHERES SO FAR
        return particles.get(0).getPosition();
    }

    public Vector3f getSize(){
        return this.entity.getSize();
    }

    public float getRadius(){
        return this.entity.getSize().y * this.entity.getScale() / 2;
    }

    public void setPosition(Vector3f position){
        particles.get(0).setPosition(position);
    }

    public void addPosition(Vector3f position){
        particles.get(0).addPosition(position);
    }

    public float getMass(){
        return this.mass;
    }

    public Entity getEntity() {
        return entity;
    }

    public void decelerateXZ(float delta){
        particles.get(0).decelerateXZ(delta);
    }

    public float getYDirection(){
        return particles.get(0).getDirection().y / Math.abs(particles.get(0).getDirection().y);
    }

    public void restY(){
        particles.get(0).restY();
    }
}
