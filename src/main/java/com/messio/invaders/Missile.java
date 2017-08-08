package com.messio.invaders;

import java.util.Random;

/**
 * Created by jpc on 11/06/14.
 */
public class Missile extends Actor {
    private final boolean type;
    private boolean deleted;

    public Missile(int x, int y) {
        super(1, x, y);
        this.type = new Random().nextBoolean();
    }

    @Override
    public String getSpriteName() {
        return state == 1 ? (type ? "missile_a:0" : "missile_b:0") : (type ? "missile_a:1" : "missile_b:1");
    }

    @Override
    public void pulse(Input input) {
        if (!isDeleted()) reset(state == 1 ? 2 : 1);
    }
}
