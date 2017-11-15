package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import utils.Maths;

import java.util.List;

/// TODO keep watching https://www.youtube.com/watch?v=yNYwZMmgTJk&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=14
public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader){
        this.shader = shader;
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix){
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    public void render(List<Terrain> terrains){
        for(Terrain terrain:terrains){
            prepareTerrain(terrain);
            loadModelMatrix(terrain);

            drawTerrain(terrain.getModel().getVertexCount());

            unbindTexturedModel();
        }
    }

    private void prepareTerrain(Terrain terrain){

        RawModel mesh = terrain.getModel();
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        ModelTexture texture = terrain.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());

    }

    private void unbindTexturedModel(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        //unbind
        GL30.glBindVertexArray(0);

    }

    private void drawTerrain(int numVertices){
        GL11.glDrawElements(GL11.GL_TRIANGLES, numVertices,
                GL11.GL_UNSIGNED_INT, 0);
    }

    private void loadModelMatrix(Terrain terrain){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), 0, terrain.getZ()),
                0, 0,0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
