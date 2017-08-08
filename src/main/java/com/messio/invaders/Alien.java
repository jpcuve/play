package com.messio.invaders;

/**
 * Created by jpc on 6/10/14.
 */
public class Alien extends Actor {
    private final String baseName;
    private int count;

    public Alien(int x, int y, final String baseName) {
        super(1, x, y);
        this.baseName = baseName;
    }

    @Override
    public String getSpriteName() {
        switch(state){
            case 1: return baseName + ":0";
            case 2: return baseName + ":1";
        }
        return "explosion";
    }

    @Override
    public void pulse(Input input) {
        count--;
        if (count < 0) switch(state){
            case 1:
                reset(2);
                count = 25;
                break;
            case 2:
                reset(1);
                count = 25;
                break;
            case 3:
                delete();
                break;
        }
    }

    public void destroy(){
        count = 5;
        state = 3;
    }
}
