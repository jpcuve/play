package com.messio.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jpc on 01/05/14.
 */
public enum Capability {
    BLEND(GL_BLEND),
    CULL_FACE(GL_CULL_FACE),
    DEPTH_TEST(GL_DEPTH_TEST);

    private final int value;

    Capability(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
