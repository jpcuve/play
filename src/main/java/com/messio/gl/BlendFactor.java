package com.messio.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jpc on 14/05/14.
 */
public enum BlendFactor {
    ZERO(GL_ZERO),
    ONE(GL_ONE),
    SRC_COLOR(GL_SRC_COLOR),
    ONE_MINUS_SRC_COLOR(GL_ONE_MINUS_SRC_COLOR),
    DST_COLOR(GL_DST_COLOR),
    ONE_MINUS_DST_COLOR(GL_ONE_MINUS_DST_COLOR),
    SRC_ALPHA(GL_SRC_ALPHA),
    ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
    DST_ALPHA(GL_DST_ALPHA),
    ONE_MINUS_DST_ALPHA(GL_ONE_MINUS_DST_ALPHA),
    CONSTANT_COLOR(GL_CONSTANT_COLOR),
    ONE_MINUS_CONSTANT_COLOR(GL_ONE_MINUS_CONSTANT_COLOR),
    CONSTANT_ALPHA(GL_CONSTANT_ALPHA),
    ONE_MINUS_CONSTANT_ALPHA(GL_ONE_MINUS_CONSTANT_ALPHA),
    SRC_ALPHA_SATURATE(GL_SRC_ALPHA_SATURATE);

    private final int value;

    BlendFactor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
