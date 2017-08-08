package com.messio.invaders;

import java.awt.*;

/**
 * Created by jpc on 6/10/14.
 */
public abstract class Actor extends Machine {
    protected int x;
    protected int y;

    protected Actor(int state, int x, int y) {
        super(state);
        this.x = x;
        this.y = y;
    }

    public abstract String getSpriteName();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Rectangle getRectangle(final SpriteShader spriteShader){
        final Sprite sprite = spriteShader.getSprite(getSpriteName());
        int w = sprite.getWidth();
        int h = sprite.getHeight();
        return new Rectangle(x - w / 2, y - h / 2, w, h);
    }

    public boolean intersects(final SpriteShader spriteShader, final Actor actor){
        return actor != null && getRectangle(spriteShader).intersects(actor.getRectangle(spriteShader));
    }
}
