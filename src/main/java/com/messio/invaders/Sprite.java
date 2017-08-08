package com.messio.invaders;

import com.messio.gl.Texture;
import com.messio.gl.TextureType;

import java.awt.image.BufferedImage;

import static com.messio.gl.FloatingPointMatrix.vec2f;

/**
 * Created by jpc on 04/06/14.
 */
public class Sprite {
    private final int width;
    private final int height;
    private final Texture texture;

    public Sprite(BufferedImage image){
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.texture = new Texture.Builder(TextureType.TWO_D).texImage(image).build();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Texture getTexture() {
        return texture;
    }
}
