package com.messio.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jpc on 23/04/14.
 */
public class Texture extends GlObject {
    public static class Builder {
        private TextureType type;
        private FilterFunction min = FilterFunction.NEAREST;
        private FilterFunction mag = FilterFunction.NEAREST;
        private Wrap s = Wrap.REPEAT;
        private Wrap t = Wrap.REPEAT;
        private boolean mipMap = true;
        private BufferedImage image;
        private int x;
        private int y;
        private int width;
        private int height;

        public Builder(TextureType type) {
            this.type = type;
        }

        public Builder filter(FilterFunction min, FilterFunction mag){
            this.min = min;
            this.mag = mag;
            return this;
        }

        public Builder wrap(Wrap s, Wrap t){
            this.s = s;
            this.t = t;
            return this;
        }

        public Builder mipMap(boolean b){
            this.mipMap = b;
            return this;
        }

        public Builder texImage(BufferedImage bufferedImage, int x, int y, int width, int height){
            this.image = bufferedImage;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder texImage(BufferedImage bufferedImage){
            return texImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        }

        public Texture build(){
            final Texture texture = new Texture(type.getValue());
            GL11.glBindTexture(type.getValue(), texture.handle);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            final ByteBuffer pixels = texture.texelData(image, x, y, width, height);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
            if (mipMap) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(type.getValue(), GL11.GL_TEXTURE_MAG_FILTER, mag.getValue());
            GL11.glTexParameteri(type.getValue(), GL11.GL_TEXTURE_MIN_FILTER, min.getValue());
            GL11.glTexParameteri(type.getValue(), GL11.GL_TEXTURE_WRAP_S, s.getValue());
            GL11.glTexParameteri(type.getValue(), GL11.GL_TEXTURE_WRAP_T, t.getValue());
            GL11.glBindTexture(type.getValue(), 0);
            return texture;
        }
    }

    final int textureType;

    public Texture(int textureType) {
        this.textureType = textureType;
        this.handle = GL11.glGenTextures();
        checkError();
    }

    private ByteBuffer texelData(BufferedImage bufferedImage, int x, int y, int width, int height){
        final int[] pixels = new int[width * height];
        bufferedImage.getRGB(x, y, width, height, pixels, 0, width);
        final int size = width * height * 4;
        final ByteBuffer buffer = BufferUtils.createByteBuffer(size);
        for (int j = 0; j < height; j++){
            for (int i = 0; i < width; i++){
                int pixel = pixels[j * width + i];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component.
            }
        }
        buffer.flip();
        return buffer;
    }

    @Override
    public void close() throws IOException {
        if (!closed){
            GL11.glDeleteTextures(this.handle);
        }
        closed = true;
    }
}
