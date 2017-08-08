package com.messio.gl;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL31;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.*;

/**
 * Created by jpc on 04/05/14.
 */
public enum TextureType {
    ONE_D(GL_TEXTURE_1D),
    TWO_D(GL_TEXTURE_2D),
    BUFFER(GL_TEXTURE_BUFFER);

    private final int value;

    TextureType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
