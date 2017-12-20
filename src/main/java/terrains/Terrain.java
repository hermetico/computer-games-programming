package terrains;

import models.RawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.Loader;
import utils.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Terrain {
    public static final float X_MIN = 0;
    public static final float X_MAX = 100;
    public static final float Z_MAX = 0;
    public static final float Z_MIN = -100;
    public static final float SIZE = 100;
    public static final Vector3f CENTER = new Vector3f(25f, 0, -25f); // X,Z
    private static final float MAX_HEIGHT = 2;// flat 50;
    private static final float MAX_PIXEL_VALUE  = 256 * 256 * 256;


    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    private float[][] heights;

    public Terrain(int gridX, int  gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap){

        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x  = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heightMap);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public float getTerrainHeight(Vector3f position){
        return getTerrainHeight(position.x, position.z);
    }

    public float getTerrainHeight(float coordX, float coordZ){
        float x = coordX - this.x, z = coordZ - this.z;
        float gridSize = SIZE / (float) (heights.length - 1);
        int gridX = (int) Math.floor(x / gridSize);
        int gridZ = (int) Math.floor(z / gridSize);
        if(gridX >= heights.length - 1 ||gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0){
            return 0;

        }
        float xCoord = (x % gridSize) / gridSize;
        float zCoord = (z % gridSize) / gridSize;
        float result;
        if (xCoord <= (1-zCoord)) {
            result = Maths.baryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            result = Maths.baryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return result;

    }
    private RawModel generateTerrain(Loader loader, String heightMap){
        BufferedImage image = null;

        try {
            image = ImageIO.read(Terrain.class.getClass().getResourceAsStream("/textures/" + heightMap +".png"));
        } catch (IOException e) {
            System.err.println("Error opening height map file");
            e.printStackTrace();
        }
         int vertex_count = image.getHeight(); 

        int count = vertex_count * vertex_count;
        heights = new float[vertex_count][vertex_count];
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(vertex_count-1)*(vertex_count-1)];
        int vertexPointer = 0;
        for(int i=0;i<vertex_count;i++){
            for(int j=0;j<vertex_count;j++){
                vertices[vertexPointer*3] = (float)j/((float)vertex_count - 1) * SIZE;
                float height = getHeight(j, i, image);
                vertices[vertexPointer*3+1] = height;
                heights[j][i] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)vertex_count - 1) * SIZE;
                Vector3f normal = calculateNormal(j,i, image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)vertex_count - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)vertex_count - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<vertex_count-1;gz++){
            for(int gx=0;gx<vertex_count-1;gx++){
                int topLeft = (gz*vertex_count)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*vertex_count)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    private Vector3f calculateNormal(int x, int z, BufferedImage image){
        float heightL = getHeight(x-1, z, image);
        float heightR = getHeight(x+1, z, image);
        float heightT = getHeight(x, z+1, image);
        float heightB = getHeight(x, z-1, image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightB - heightT);
        normal.normalize();
        return normal;
    }

    private float getHeight(int x, int z, BufferedImage image){
        if(x < 0 || x >= image.getHeight() || z < 0 || z>= image.getHeight()){
            return 0;
        }
        float height = image.getRGB(x, z);
        height += MAX_PIXEL_VALUE / 2f;
        height /= MAX_PIXEL_VALUE / 2f;
        height *= MAX_HEIGHT;
        return height;
    }
}
