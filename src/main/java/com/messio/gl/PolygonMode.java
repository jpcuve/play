package com.messio.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jpc on 07/05/14.
 */
public enum PolygonMode {
    POINT(GL_POINT),
    LINE(GL_LINE),
    FILL(GL_FILL);

    private final int value;

    PolygonMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
