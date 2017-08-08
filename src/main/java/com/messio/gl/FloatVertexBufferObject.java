package com.messio.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;

/**
 * Created by jpc on 4/22/14.
 */
public class FloatVertexBufferObject extends VertexBufferObject {

    public FloatVertexBufferObject() {
        super(GL11.GL_FLOAT);
    }

    public void bufferData(int vectorSize, double... data){
        upload(new FloatingPointMatrix(vectorSize, data).getFloatBuffer());
        this.vectorSize = vectorSize;
        this.elementCount = data.length / vectorSize;
    }

    public void buffer(FloatingPointMatrix... mats){
        this.elementCount = mats.length;
        if (this.elementCount > 0){
            this.vectorSize = mats[0].rowDim * mats[0].colDim;
            final FloatBuffer buffer = BufferUtils.createFloatBuffer(this.elementCount * this.vectorSize);
            for (FloatingPointMatrix mat: mats) buffer.put(mat.getFloatBuffer());
            buffer.flip();
            upload(buffer);
        }
    }

    private void upload(FloatBuffer buffer){
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        checkError();
    }

    @Override
    public int getElementCount() {
        return elementCount;
    }
}
