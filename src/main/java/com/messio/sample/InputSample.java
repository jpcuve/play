package com.messio.sample;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.IllegalFormatException;
import java.util.Scanner;

/**
 * Created by jpc on 20/04/14.
 */
public class InputSample {
    private static final int FPS = 50;

    public static String getResourceAsString(String resourceName) throws IOException {
        try (final Scanner scanner = new Scanner(InputSample.class.getClassLoader().getResourceAsStream(resourceName)).useDelimiter("\\A");){
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private static void checkError(final String message){
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR){
            switch(error){
                case GL11.GL_INVALID_ENUM:
                    throw new IllegalArgumentException("Invalid enum, inappropriate: " + message);
                case GL11.GL_INVALID_VALUE:
                    throw new IllegalArgumentException("Invalid value, out of range: " + message);
                case GL11.GL_INVALID_OPERATION:
                    throw new IllegalArgumentException("Invalid operation, not allowed in this context or in the current state: " + message);
                case GL11.GL_OUT_OF_MEMORY:
                    throw new OutOfMemoryError("Out of graphics memory:" + message);
            }
        }
    }

    private static int createShader(int shaderType, String resourceName) throws IOException {
        final String source = getResourceAsString(resourceName);
        int handle = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(handle, source);
        GL20.glCompileShader(handle);
        int status = GL20.glGetShaderi(handle, GL20.GL_COMPILE_STATUS);
        if (status == GL11.GL_FALSE){
            int infoLength = GL20.glGetShaderi(handle, GL20.GL_INFO_LOG_LENGTH);
            final String infoLog = GL20.glGetShaderInfoLog(handle, infoLength);
            switch(shaderType){
                case GL20.GL_VERTEX_SHADER:
                    throw new IllegalArgumentException("Vertex shader compilation error: " + infoLog);
                case GL32.GL_GEOMETRY_SHADER:
                    throw new IllegalArgumentException("Geometry shader compilation error: " + infoLog);
                case GL20.GL_FRAGMENT_SHADER:
                    throw new IllegalArgumentException("Fragment shader compilation error: " + infoLog);
            }
        }
        return handle;
    }

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Input sample");
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        final float points[] = {
                0.0f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f
        };


        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(points.length);
        floatBuffer.put(points);
        floatBuffer.flip();
        int vertexCount = points.length / 3;
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        int vs = createShader(GL20.GL_VERTEX_SHADER, "input_sample_vs.glsl");
        int fs = createShader(GL20.GL_FRAGMENT_SHADER, "input_sample_fs.glsl");
        int pgm = GL20.glCreateProgram();
        GL20.glAttachShader(pgm, fs);
        GL20.glAttachShader(pgm, vs);
        GL20.glLinkProgram(pgm);
        GL20.glUseProgram(pgm);

        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
        checkError("Setup terminated");

        while (!Display.isCloseRequested()){
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL30.glBindVertexArray(vao);
            GL20.glEnableVertexAttribArray(0);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);

            Display.sync(FPS);
            Display.update();
        }
        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vbo);
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vao);
        Display.destroy();
    }
}
