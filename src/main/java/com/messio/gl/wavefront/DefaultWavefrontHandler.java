package com.messio.gl.wavefront;

import com.messio.gl.FloatingPointMatrix;
import com.messio.gl.IntegerMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpc on 5/13/14.
 */
public class DefaultWavefrontHandler implements WavefrontHandler {
    private final Map<Integer, FloatingPointMatrix> vertexes = new HashMap<>();
    private final List<IntegerMatrix> faces = new ArrayList<>();

    @Override
    public void object(String objectName) {

    }

    @Override
    public void group(String groupName) {

    }

    @Override
    public void vertex(int vertexIndex, double x, double y, double z, double w) {
        vertexes.put(vertexIndex, FloatingPointMatrix.vec3f(x, y, z));
    }

    @Override
    public void texture(int textureIndex, double u, double v, double w) {

    }

    @Override
    public void normal(int normalIndex, double x, double y, double z) {

    }

    @Override
    public void triangle(int[] v1, int[] v2, int[] v3) {
        faces.add(IntegerMatrix.vec3i(v1[0], v2[0], v3[0]));
    }

    public FloatingPointMatrix[] getTriangleVertexes(){
        final FloatingPointMatrix[] vs = new FloatingPointMatrix[faces.size() * 3];
        for (int i = 0; i < faces.size(); i++){
            vs[i * 3] = vertexes.get(faces.get(i).getX());
            vs[i * 3 + 1] = vertexes.get(faces.get(i).getY());
            vs[i * 3 + 2] = vertexes.get(faces.get(i).getZ());
        }
        return vs;
    }

}
