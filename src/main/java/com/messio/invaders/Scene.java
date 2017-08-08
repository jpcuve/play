package com.messio.invaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jpc on 6/10/14.
 */
public abstract class Scene {
//    private final Gallery gallery = new Gallery("invaders/sprite.txt");
    protected final int gameWidth;
    protected final int gameHeight;
    protected final FontShader fontShader;
    protected final SpriteShader spriteShader;

    public Scene(int gameWidth, int gameHeight, FontShader fontShader, SpriteShader spriteShader) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.fontShader = fontShader;
        this.spriteShader = spriteShader;
    }

    public abstract void init();
    public abstract void pulse(Input input);
    public abstract void paint();
    public abstract void done();

}
