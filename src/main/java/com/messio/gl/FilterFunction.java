package com.messio.gl;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by jpc on 5/6/14.
 */
public enum FilterFunction {
    NEAREST(GL_NEAREST),
    LINEAR(GL_LINEAR),
    NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
    LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
    NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
    LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR);

    private final int value;

    FilterFunction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
