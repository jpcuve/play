package com.messio.invaders;

/**
 * Created by jpc on 6/19/14.
 */
public class Input {
    private final boolean left;
    private final boolean right;
    private final boolean space;

    public Input(boolean left, boolean right, boolean space) {
        this.left = left;
        this.right = right;
        this.space = space;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isSpace() {
        return space;
    }
}
