package com.messio.gl;

import org.lwjgl.opengl.GL11;

/**
 * Created by jpc on 24/04/14.
 */
public enum Buffer {
    COLOR(GL11.GL_COLOR_BUFFER_BIT),
    DEPTH(GL11.GL_DEPTH_BUFFER_BIT),
    STENCIL(GL11.GL_STENCIL_BUFFER_BIT);

    private final int value;

    Buffer(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
