package renderEngine;

import physics.AABB;
import entities.Entity;
import entities.extensions.Selectable;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import shaders.BoundingBoxShader;
import shaders.StaticShader;
import textures.ModelTexture;
import utils.Maths;

import java.util.List;
import java.util.Map;

public class EntityRenderer {

    private StaticShader shader;
    private BoundingBoxShader bShader;

    public EntityRenderer(StaticShader shader, BoundingBoxShader bShader){
        this.shader = shader;
        this.bShader = bShader;
    }

    public void render(Map<TexturedModel, List<Entity>> entities){
        for(TexturedModel model: entities.keySet()){
            prepareTexturedModel(model);
            List<Entity> batch  = entities.get(model);

            for(Entity entity:batch){
                prepareInstance(entity);
                drawInstance(model.getRawEntity().getVertexCount());


            }
            unbindTexturedModel();
        }
    }


    public void renderBoundingBox(Selectable selectable){

        bShader.start();


        AABB box = selectable.getAABB();
        GL30.glBindVertexArray(box.getVAOID());
        GL20.glEnableVertexAttribArray(0); // AABB positions

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(box.getPosition(),
                box.getRotation().x, box.getRotation().y, box.getRotation().z, box.getScale());

        bShader.loadTransformationMatrix(transformationMatrix);


        GL11.glDrawElements(GL11.GL_LINES, box.getCount(),
                GL11.GL_UNSIGNED_INT, 0);




        bShader.stop();

    }



    private void prepareTexturedModel(TexturedModel model){

        RawModel mesh = model.getRawEntity();
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());

        if(texture.isHasTransparency()){
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());

    }

    private void unbindTexturedModel(){
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        //unbind
        GL30.glBindVertexArray(0);

    }

    private void drawInstance(int numVertices){
        GL11.glDrawElements(GL11.GL_TRIANGLES, numVertices,
                GL11.GL_UNSIGNED_INT, 0);
    }


    private void prepareInstance(Entity entity){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix){
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();

        bShader.start();
        bShader.loadProjectionMatrix(projectionMatrix);
        bShader.stop();
    }


}
