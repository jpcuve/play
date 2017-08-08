package com.messio.gl;

import org.lwjgl.opengl.GL11;

import java.io.Closeable;

/**
 * Created by jpc on 4/22/14.
 */
public abstract class GlObject implements Closeable {
    int handle = 0;
    boolean closed = false;

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public static void checkError(final String message){
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

    public static void checkError(){
        checkError(new Object(){}.getClass().getEnclosingMethod().getName());
    }

}
