package com.messio.invaders;

/**
 * Created by jpc on 6/17/14.
 */
public class Block extends Actor {

    public Block(int x, int y) {
        super(4, x, y);
    }

    @Override
    public String getSpriteName() {
        return "block:" + state;
    }


    @Override
    public void pulse(Input input) {
    }

    public void destroy(){
        state--;
    }

}
