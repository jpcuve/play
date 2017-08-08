package com.messio.gl;

import org.lwjgl.opengl.*;

/**
 * Created by jpc on 5/6/14.
 */
public enum Wrap {
    CLAMP_TO_EDGE(0),
    MIRRORED_REPEAT(0),
    REPEAT(GL11.GL_REPEAT);

    private final int value;

    Wrap(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
