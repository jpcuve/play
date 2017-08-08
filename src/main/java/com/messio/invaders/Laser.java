package com.messio.invaders;

/**
 * Created by jpc on 10/06/14.
 */
public class Laser extends Actor {
    public Laser(int x, int y) {
        super(1, x, y);
    }

    @Override
    public String getSpriteName() {
        return "laser";
    }

    @Override
    public void pulse(Input input) {

    }
}
