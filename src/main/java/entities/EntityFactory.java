package entities;

import entities.extensions.Selectable;
import models.RawEntity;
import models.TexturedModel;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;
import renderEngine.Loader;
import textures.ModelTexture;
import utils.OBJC.ModelData;
import utils.OBJC.OBJFileLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityFactory {
    private static EntityFactory instance;
    private List<Entity> bullets;
    private List<RigidBody> cubes;
    private List<Entity> visible;
    List<Selectable> selectables;
    private Loader loader = new Loader();
    private PhysicsEngine physics;

    private EntityFactory(){}

    public void init(List<Entity> bullets, List<RigidBody> cubes, List<Entity> visible, List<Selectable> selectables){
        physics = PhysicsEngine.getInstance();
        this.bullets = bullets;
        this.cubes = cubes;
        this.visible = visible;
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
        Bullet bullet = new Bullet(bulletTxModel, position, 0,0, 0,.8f, old_position);
        this.bullets.add(bullet);
        this.selectables.add(bullet);
        physics.getBullets().add(bullet.getBody());

    }
    public void createCube(){

        // FERNS
        //ModelData dataFern = OBJFileLoader.loadOBJ("fern");
        ModelData dataFern = OBJFileLoader.loadOBJ("cube");
        RawEntity cube = loader.loadToVAO(dataFern);

        //ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("fernAtlas"));
        //fernAtlasTexture.setNumberOfRows(2);
        ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("purple"));
        TexturedModel fernModel = new TexturedModel(cube,fernAtlasTexture);


        Random random = new Random();

        for(int i = 0; i < 500; i++){

            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * -200;
            float y = random.nextFloat() * 100;

            Entity n = new Entity(fernModel, new Vector3f(x,y,z),
                    0,
                    random.nextFloat() * 180f,
                    0,
                    1f,
                    random.nextInt(4));
            n.setEntityDescription("cube " + i);
            visible.add(n);
            RigidBody m = new RigidBody(PhysicsEngine.OBJECT_SPHERE,n ,30);
            cubes.add(m);
            physics.getCubes().add(m);
            this.selectables.add(n);
        }
    }

    public Player createPlayer(){

        ModelData bunnyData = OBJFileLoader.loadOBJ("sphere");
        RawEntity bunnyEntity = loader.loadToVAO(bunnyData);
        TexturedModel bunny = new TexturedModel(bunnyEntity, new ModelTexture(
                loader.loadTexture("purple")));
        Player player = new Player(bunny, new Vector3f(5, 15, -5), 0,90, 0,1f);
        Light jetLight = createJetPackLight();
        player.setJetLight(jetLight);
        physics.setPlayer(player.getBody());
        visible.add(player.getEntity());
        this.selectables.add(player.getEntity());
        return player;

    }



    public static EntityFactory getInstance(){
        if(instance == null){
            instance = new EntityFactory();
        }
        return instance;
    }
}
