package com.messio.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jpc on 4/23/14.
 */
public enum DrawMode {
    POINTS(GL_POINTS),
    LINE_STRIP(GL_LINE_STRIP),
    LINE_LOOP(GL_LINE_LOOP),
    LINES(GL_LINES),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN),
    TRIANGLES(GL_TRIANGLES);

    private final int value;

    DrawMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
