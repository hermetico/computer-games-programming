package renderEngine;

import de.matthiasmann.twl.utils.PNGDecoder;
import entities.BoundingBox;
import models.RawEntity;
import models.RawModel;
import org.lwjgl.opengl.*;
import textures.Texture;
import org.lwjgl.BufferUtils;
import textures.TextureData;
import utils.OBJC.ModelData;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;


public class Loader {
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public RawEntity loadToVAO(ModelData model){

        int vaoID = createVAO();
        RawEntity resultEntity = new RawEntity(vaoID, model.getIndices().length, model.getVertices());
        BoundingBox entityBounding = resultEntity.getBoundingBox();

        bindIndicesBuffer(model.getIndices());
        // data positions is in attribute 0
        storeDataInAttributeList(0, 3, model.getVertices());
        storeDataInAttributeList(1, 2, model.getTextureCoords());
        storeDataInAttributeList(2, 3, model.getNormals());
        unbindVAO();

        vaoID = createVAO();
        entityBounding.setVAOID(vaoID);
        bindIndicesBuffer(entityBounding.getBoundingIndices());
        storeDataInAttributeList(0, 3, entityBounding.getBoundingPositions());
        unbindVAO();

        return  resultEntity;
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords,float[] normals, int[] indices ){
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        // data positions is in attribute 0
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }


    public RawModel loadToVAO( float[] positions, int dimensions){
        int vaoID = createVAO();
        this.storeDataInAttributeList(0,dimensions, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimensions);
    }


    public int loadTexture(String fileName){
        Texture texture = null;
        try {
            texture = new Texture("/textures/" + fileName + ".png");
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int textureID = texture.getId();
        textures.add(textureID);
        return textureID;
    }

    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream("/textures/" + fileName + ".png"));
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to load texture " + fileName);
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    public int loadCubeMap( String[] textureFiles){
        int textID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textID);
        for(int i = 0; i < textureFiles.length; i++){
            TextureData data = decodeTextureFile( textureFiles[i]);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, data.getBuffer());
        }
        GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        textures.add(textID);
        return textID;
    }

    public void cleanUp(){
        for(int  vao: vaos){
            GL30.glDeleteVertexArrays(vao);
        }

        for(int  vbo: vbos){
            GL15.glDeleteBuffers(vbo);
        }

        for(int texture:textures){
            GL11.glDeleteTextures(texture);
        }
    }

    private int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFLoatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0,0);
        // unbind the current vbo at the end
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15. GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;

    }

    private FloatBuffer storeDataInFLoatBuffer(float [] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

        buffer.put(data);
        // we have written in the buffer
        // flip it in order to be ready for be read
        buffer.flip();
        return buffer;
    }
}
