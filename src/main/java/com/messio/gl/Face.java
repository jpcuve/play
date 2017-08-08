package com.messio.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jpc on 07/05/14.
 */
public enum Face {
    FRONT_AND_BACK(GL_FRONT_AND_BACK),
    FRONT(GL_FRONT),
    BACK(GL_BACK);

    private final int value;

    Face(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
