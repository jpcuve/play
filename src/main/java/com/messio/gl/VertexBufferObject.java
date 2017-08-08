package com.messio.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by jpc on 4/22/14.
 */
public abstract class VertexBufferObject extends GlObject {
    final int elementType;
    int vectorSize;
    int elementCount;

    protected VertexBufferObject(int elementType) {
        this.elementType = elementType;
        this.handle = GL15.glGenBuffers();
        checkError();
    }

    public abstract int getElementCount();

    @Override
    public void close() throws IOException {
        if (!closed){
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(handle);
        }
        closed = true;
    }
}
