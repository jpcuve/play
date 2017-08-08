package com.messio.gl.wavefront;

/**
 * Created by jpc on 5/13/14.
 */
public interface WavefrontHandler {
    void object(String objectName);
    void group(String groupName);
    void vertex(int vertexIndex, double x, double y, double z, double w);
    void texture(int textureIndex, double u, double v, double w);
    void normal(int normalIndex, double x, double y, double z);
    void triangle(int[] v1, int[] v2, int[] v3);
}
