package com.messio.invaders;

/**
 * Created by jpc on 6/10/14.
 */
public class Base extends Actor {

    public Base(int x, int y) {
        super(1, x, y);
    }

    @Override
    public String getSpriteName() {
        return "base";
    }

    @Override
    public void pulse(Input input) {
    }
}
