package Factories;

import entities.*;
import entities.extensions.Selectable;
import models.RawEntity;
import models.TexturedModel;
import org.joml.Vector3f;
import physics.PhysicsEngine;
import physics.RigidBody;
import renderEngine.Loader;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import textures.ModelTexture;
import utils.OBJC.ModelData;
import utils.OBJC.OBJFileLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Factory {

    private static final int NUM_CLOUDS = 300;
    private static Factory instance;
    private List<RigidBody> cubes;
    private List<Entity> visible;
    List<Selectable> selectables;
    private Loader loader = new Loader();
    private PhysicsEngine physics;
    private Terrain terrain;

    private Factory(){}

    public void init(List<RigidBody> cubes, List<Entity> visible, List<Selectable> selectables){
        physics = PhysicsEngine.getInstance();
        this.cubes = cubes;
        this.visible = visible;
        this.selectables = selectables;


    }

    public List<Light> createGameLights(){
        List<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(0,100,0), new Vector3f(0.8f,0.8f,0.8f)));
        lights.add(new Light(new Vector3f(50,500,-50), new Vector3f(1,1,1), new Vector3f(0.4f, 0.4f, 0.4f)));
        return lights;
    }

    public Light createJetPackLight(){
        return new Light(new Vector3f(0,0,0), new Vector3f(1,1,1), new Vector3f(0.05f, 0.05f, 0.05f));
    }

    public void createBullet( Vector3f position, Vector3f old_position){
        ModelData bulletModel = OBJFileLoader.loadOBJ("sphere");
        RawEntity bulletEntity = loader.loadToVAO(bulletModel);
        TexturedModel bulletTxModel = new TexturedModel(bulletEntity, new ModelTexture(loader.loadTexture("trencadis")));
        Bullet bullet = new Bullet(bulletTxModel, position, 0,0, 0,.8f, old_position);
        this.visible.add(bullet);
        this.selectables.add(bullet);
        physics.getBullets().add(bullet.getBody());

    }
    public void createCube(){


        ModelData dataFern = OBJFileLoader.loadOBJ("cloud");
        RawEntity cube = loader.loadToVAO(dataFern);

        //ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("fernAtlas"));
        //fernAtlasTexture.setNumberOfRows(2);
        ModelTexture fernAtlasTexture = new ModelTexture(loader.loadTexture("white"));
        TexturedModel fernModel = new TexturedModel(cube,fernAtlasTexture);


        Random random = new Random();

        for(int i = 0; i < NUM_CLOUDS; i++){

            float x = (terrain.X_MAX / 2) + random.nextFloat() * terrain.X_MAX / 2;
            float z = (terrain.Z_MIN / 2) +random.nextFloat() * terrain.Z_MIN / 2;
            float y = random.nextFloat() * 200;

            Cloud c = new Cloud(fernModel, new Vector3f(x,y,z),
                    0,
                    0,
                    0,
                    1f,
                    terrain.CENTER);
            c.setEntityDescription("cube " + i);
            visible.add(c);
            RigidBody m = new RigidBody(PhysicsEngine.OBJECT_SPHERE,c ,30);
            c.setBody(m); // circular reference ! :(
            cubes.add(m);
            physics.getCubes().add(m);
            this.selectables.add(c);
        }
    }

    public Terrain createTerrain(){
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("tiles"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack= new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        terrain = new Terrain(0,-1, loader, texturePack, blendMap, "heightmap");
        return terrain;
    }
    public Player createPlayer(){

        ModelData bunnyData = OBJFileLoader.loadOBJ("penguin");
        RawEntity bunnyEntity = loader.loadToVAO(bunnyData);
        TexturedModel bunny = new TexturedModel(bunnyEntity, new ModelTexture(
                loader.loadTexture("penguin")));
        Player player = new Player(bunny, new Vector3f(5, 15, -5), 0,90, 0,1f);
        Light jetLight = createJetPackLight();
        player.setJetLight(jetLight);
        physics.setPlayer(player.getBody());
        visible.add(player.getEntity());
        this.selectables.add(player.getEntity());
        return player;

    }



    public static Factory getInstance(){
        if(instance == null){
            instance = new Factory();
        }
        return instance;
    }


    public void removeVisible(Entity entity){
        visible.remove(entity);
    }

    public void removeCube(RigidBody cube){
        cubes.remove(cube);
    }

    public void hideCubesBelow( float height){
        for (Iterator<RigidBody> iterator = physics.getCubes().iterator(); iterator.hasNext();) {
            RigidBody body = iterator.next();
            if (body.getEntity().getAABB().getMax().y < height){
                iterator.remove();
                visible.remove(body.getEntity());
            }
        }
    }

    public void restoreAllCubes(){
        for(RigidBody cube : cubes){
            if(!physics.getCubes().contains(cube)) {
                physics.getCubes().add(cube);
                visible.add(cube.getEntity());
            }
        }

    }
}
