package com.messio.gl;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.util.*;

/**
 * Created by jpc on 4/22/14.
 */
public class VertexArrayObject extends GlObject {
    public static class Builder {
        final Map<Integer, VertexBufferObject> vbos = new HashMap<>();

        public Builder floatBuffer(int index, FloatingPointMatrix... matrices){
            if (vbos.containsKey(index) || index < 0 || index > 15) throw new IllegalArgumentException();
            final FloatVertexBufferObject vbo = new FloatVertexBufferObject();
            vbo.buffer(matrices);
            vbos.put(index, vbo);
            return this;
        }

        public Builder byteBuffer(int index, IntegerMatrix... matrices){
            if (vbos.containsKey(index) || index < 0 || index > 15) throw new IllegalArgumentException();
            final ByteVertexBufferObject vbo = new ByteVertexBufferObject();
            vbo.buffer(matrices);
            vbos.put(index, vbo);
            return this;
        }

        public VertexArrayObject build(){
            final VertexArrayObject vao = new VertexArrayObject();
            GL30.glBindVertexArray(vao.handle);
            for (final Map.Entry<Integer, VertexBufferObject> entry: vbos.entrySet()){
                final VertexBufferObject vbo = entry.getValue();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo.handle);
                GL20.glVertexAttribPointer(entry.getKey(), vbo.vectorSize, vbo.elementType, false, 0, 0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                vao.vbos[entry.getKey()] = vbo;
            }
            GL30.glBindVertexArray(0);
            return vao;
        }
    }

    private final VertexBufferObject[] vbos = new VertexBufferObject[16];

    public VertexArrayObject() {
        this.handle = GL30.glGenVertexArrays();
        checkError();
    }

    public void setVertexBufferObject(int position, VertexBufferObject vbo){
        if (position < 0 || position > 15) throw new IllegalArgumentException();
        GL30.glBindVertexArray(handle);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo.handle);
        GL20.glVertexAttribPointer(position, vbo.vectorSize, vbo.elementType, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        vbos[position] = vbo;
    }

    public int getElementCount(){
        for (VertexBufferObject vbo: vbos) if (vbo != null) return vbo.getElementCount();
        return -1;
    }

    public void enableAllVertexAttribArrays(){
        for (int i = 0; i < vbos.length; i++) if (vbos[i] != null) GL20.glEnableVertexAttribArray(i);
    }

    public void disableAllVertexAttribArrays(){
        for (int i = 0; i < vbos.length; i++) if (vbos[i] != null) GL20.glDisableVertexAttribArray(i);
    }

    public void enableVertexAttribArray(Integer... indices){
        final List<Integer> list = Arrays.asList(indices);
        for (int i = 0; i < vbos.length; i++){
            boolean b = list.contains(i);
            if (b && vbos[i] == null) throw new IllegalArgumentException("Invalid index: " + i);
            if (b) GL20.glEnableVertexAttribArray(i); else GL20.glDisableVertexAttribArray(i);
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed){
            for (int i = 0; i < vbos.length; i++) if (vbos[i] != null){
                GL20.glDisableVertexAttribArray(i);
                vbos[i].close();
            }
            GL30.glBindVertexArray(0);
            GL30.glDeleteVertexArrays(this.handle);
        }
        this.closed = true;
    }
}
