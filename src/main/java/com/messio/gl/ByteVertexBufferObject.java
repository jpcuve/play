package com.messio.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

/**
 * Created by jpc on 4/22/14.
 */
public class ByteVertexBufferObject extends VertexBufferObject {

    public ByteVertexBufferObject() {
        super(GL11.GL_UNSIGNED_BYTE);
    }

    public void bufferData(int vectorSize, int... data){
        upload(new IntegerMatrix(vectorSize, data).getByteBuffer());
        this.vectorSize = vectorSize;
        this.elementCount = data.length / vectorSize;
    }

    public void buffer(IntegerMatrix... mats){
        this.elementCount = mats.length;
        if (this.elementCount > 0){
            this.vectorSize = mats[0].rowDim * mats[0].colDim;
            final ByteBuffer buffer = BufferUtils.createByteBuffer(this.elementCount * this.vectorSize);
            for (IntegerMatrix mat: mats) buffer.put(mat.getByteBuffer());
            buffer.flip();
            upload(buffer);
        }
    }

    public void buffer(int... bytes){
        this.elementCount = bytes.length;
        final ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        for (int b: bytes) buffer.put((byte) b);
        buffer.flip();
        upload(buffer);
        this.vectorSize = 1;
    }

    private void upload(ByteBuffer buffer){
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
