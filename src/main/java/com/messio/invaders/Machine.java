package com.messio.invaders;

/**
 * Created by jpc on 6/24/14.
 */
public abstract class Machine {
    protected int state;

    public Machine(int state) {
        this.state = state;
    }

    public boolean isDeleted(){
        return state == 0;
    }

    public void reset(int state){
        this.state = state;
    }

    public void delete(){
        reset(0);
    }

    public abstract void pulse(Input input);

}
