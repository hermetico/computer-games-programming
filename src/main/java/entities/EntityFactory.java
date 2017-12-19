package entities;

import entities.extensions.Selectable;
import models.RawEntity;
import models.TexturedModel;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import renderEngine.Loader;
import textures.ModelTexture;
import utils.OBJC.ModelData;
import utils.OBJC.OBJFileLoader;

import java.util.ArrayList;
import java.util.List;

public class EntityFactory {
    private static EntityFactory instance;
    private List<Entity> bullets;
    List<Selectable> selectables;
    private Loader loader = new Loader();
    private PhysicsEngine physics;

    private EntityFactory(){}

    public void init(List<Entity> bullets, List<Selectable> selectables){
        physics = PhysicsEngine.getInstance();
        this.bullets = bullets;
        this.selectables = selectables;

    }

    public List<Light> createGameLights(){
        List<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(0,10000,-7000), new Vector3f(0.4f,0.4f,0.4f)));
        return lights;
    }

    public Light createJetPackLight(){
        return new Light(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0.1f, 0.01f, 0.1f));
    }

    public void createBullet( Vector3f position, Vector3f old_position){
        ModelData bulletModel = OBJFileLoader.loadOBJ("sphere");
        RawEntity bulletEntity = loader.loadToVAO(bulletModel);
        TexturedModel bulletTxModel = new TexturedModel(bulletEntity, new ModelTexture(loader.loadTexture("trencadis")));
        Bullet bullet = new Bullet(bulletTxModel, position, 0,0, 0,.5f, old_position);
        this.bullets.add(bullet);
        this.selectables.add(bullet);
        physics.getBullets().add(bullet.getBody());

    }

    public static EntityFactory getInstance(){
        if(instance == null){
            instance = new EntityFactory();
        }
        return instance;
    }
}
