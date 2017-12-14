package entities;

import models.TexturedModel;
import org.joml.Vector3f;
import terrains.Terrain;


public class Enemy extends Entity {

    private static final float RUN_SPEED = 50;
    private static final float TURN_SPEED  = 560;
    private static final float yOffset = -90; // model y rotation offset
    private static float GRAVITY = -95;
    private static final float JUMP_POWER = 45;

    private float currentSpeed = 20;
    private float currentTurnSpeed = 100;
    private float upwardsSpeed = 50;
    private boolean alreadyJumping = false;


    public Enemy(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void update (float interval, Terrain terrain, Vector3f player_position) {

        if(!check_distance_to_player(player_position)) {
            float steering = (float) (random_binominal() * currentTurnSpeed);
            if (true) {
                this.jump();
            }

            super.increaseRotation(0, steering * interval, 0);
            float distance = currentSpeed * interval;
            float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY() + yOffset)));
            float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY() + yOffset)));


            if (!collision()) {
                super.increasePosition(dx, 0, dz);
                upwardsSpeed += GRAVITY * interval;
                super.increasePosition(0, upwardsSpeed * interval, 0);


            } else {
                upwardsSpeed += GRAVITY * interval;
                super.increasePosition(-dx, 0, -dz);
            }

            float terrainHeight = terrain.getTerrainHeight(super.getPosition().x, super.getPosition().z);

            if(super.landingOnTerrain(terrainHeight)){
                upwardsSpeed = 0;
                alreadyJumping = false;
            }

        }
        else{
            chase();
        }

    }


    private void jump(){
        if(!alreadyJumping) {
            this.upwardsSpeed = JUMP_POWER;
            alreadyJumping = true;
        }
    }

    public float getyOffset(){
        return yOffset;
    }

    private boolean collision() {
        return false;
    }
    private double random_binominal(){
        return Math.random() - Math.random();
    }

    private boolean check_distance_to_player(Vector3f player_position) {
        double x_dist = Math.abs(this.position.x) - Math.abs(player_position.x);
        double y_dist = Math.abs(this.position.y) - Math.abs(player_position.y);
        boolean alert = false;
        if(x_dist < 2 && y_dist < 2){
            alert = true;
        }
        return alert;
    }

    private void chase(){
    }
}

