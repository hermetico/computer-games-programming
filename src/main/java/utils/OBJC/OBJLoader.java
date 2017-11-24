package utils.OBJC;

import models.RawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.Loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class OBJLoader {
    public static RawModel loadObjModel(String fileName, Loader loader) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(OBJLoader.class
                     .getClass().getResourceAsStream("/models/" + fileName +".obj")));
        } catch (Exception e) {
            System.err.println("Couldn't load the file!");
            e.printStackTrace();
        }
        String line = null;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        try {
            boolean keep = true;
            while (keep) {

                line = br.readLine();
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "v":
                        // Geometric vertex
                        Vector3f vec3f = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]));
                        vertices.add(vec3f);
                        break;
                    case "vt":
                        // Texture coordinate
                        Vector2f vec2f = new Vector2f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]));
                        textures.add(vec2f);
                        break;
                    case "vn":
                        // Vertex normal
                        Vector3f vec3fNorm = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]));
                        normals.add(vec3fNorm);
                        break;
                    case "f":
                        textureArray = new float[vertices.size() * 2];
                        normalsArray = new float[vertices.size() * 3];
                        keep = false;
                        break;
                    default:
                        // Ignore other lines
                        break;
                }
            }

            while (line != null) {
                if(!line.startsWith("f ")){
                    line = br.readLine();
                    continue;
                }
                String[] tokens = line.split("\\s+");
                String[] vertex1 = tokens[1].split("/");
                String[] vertex2 = tokens[2].split("/");
                String[] vertex3 = tokens[3].split("/");

                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);

                line = br.readLine();
            }
            br.close();

        } catch (Exception e) {
            System.err.println("Invalid file format");
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;

        for(Vector3f vertex:vertices){
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for(int i= 0; i < indices.size(); i++){
            indicesArray[i] = indices.get(i);
        }

        return loader.loadToVAO(verticesArray, textureArray, normalsArray,  indicesArray);

    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, float[] normalsArray){
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentText = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentText.x;
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentText.y;
        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
    }

}


