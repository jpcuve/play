package com.messio.invaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jpc on 6/19/14.
 */
public class GameScene extends Scene {
    private final Random random = new Random();
    private final List<Alien> aliens = new ArrayList<>();
    private Base base;
    private Laser laser;
    private final List<Missile> missiles = new ArrayList<>();
    private final List<Block> blocks = new ArrayList<>();

    public GameScene(int gameWidth, int gameHeight, FontShader fontShader, SpriteShader spriteShader) {
        super(gameWidth, gameHeight, fontShader, spriteShader);
        init();
    }

    @Override
    public void init() {
        base = new Base(0, -gameHeight / 2 + spriteShader.getSpriteSize("base").height);
        final String[] types = { "alien_a", "alien_b", "alien_c", "alien_d", "alien_e" };
        for (int row = 0; row < types.length; row++){
            for (int col = 0; col < 9; col++){
                int x = (col - 4) * 16;
                int y = row * 16;
                aliens.add(new Alien(x, y, types[row]));
            }
        }
        for (int i = 0; i < 4; i++){
            final Bunker bunker = new Bunker((2 * i - 3) * gameWidth / 8, -gameHeight / 3, spriteShader.getSpriteSize("block:0"));
            blocks.addAll(bunker.getBlocks());
        }
    }

    private List<Actor> getActors(){
        final List<Actor> actors = new ArrayList<>();
        actors.addAll(aliens);
        if (base != null) actors.add(base);
        if (laser != null) actors.add(laser);
        actors.addAll(missiles);
        actors.addAll(blocks);
        return actors;
    }

    @Override
    public void pulse(Input input) {
        for (final Actor actor: getActors()) actor.pulse(input);
        int maxBaseExtent = (gameWidth - spriteShader.getSpriteSize(base.getSpriteName()).width) / 2;
        int x = base.getX();
        if (input.isLeft()){
            x--;
            if (x < -maxBaseExtent) x = -maxBaseExtent;
        }
        if (input.isRight()){
            x++;
            if (x > maxBaseExtent) x = maxBaseExtent;
        }
        base.setX(x);
        for (final Missile missile: missiles){
            int y = missile.getY();
            int h = spriteShader.getSpriteSize(missile.getSpriteName()).height;
            y -= h / 2;
            missile.setY(y);
            if (y < -gameHeight / 2) missile.delete();
        }
        if (laser != null){
            int y = laser.getY();
            int h = spriteShader.getSpriteSize(laser.getSpriteName()).height;
            y += h;
            laser.setY(y);
            if (y > gameHeight / 2) laser = null;
        } else {
            if (input.isSpace()) laser = new Laser(base.getX(), base.getY());
        }
        if (missiles.size() < 8 && random.nextInt(4) == 0 && aliens.size() > 0){
            final Alien alien = aliens.get(random.nextInt(aliens.size()));
            final Missile missile = new Missile(alien.getX(), alien.getY());
            missiles.add(missile);
        }
        for (final Alien alien: aliens) if (laser != null && laser.intersects(spriteShader, alien)){
            alien.destroy();
            laser = null;
        }
        for (final Missile missile: missiles) if (laser != null && laser.intersects(spriteShader, missile)){
            missile.delete();
            laser = null;
        }
        for (final Block block: blocks){
            if (laser != null && !block.isDeleted() && laser.intersects(spriteShader, block)){
                block.destroy();
                laser = null;
            }
        }
        for (final Missile missile: missiles) for (final Block block: blocks) if (!block.isDeleted() && missile.intersects(spriteShader, block)){
            block.destroy();
            missile.delete();
        }
    }

    @Override
    public void paint() {
        fontShader.paint("Space Invaders", -fontShader.strlen("Space Invaders") / 2, gameHeight / 2 - 30, 3);
        for (final Actor actor: getActors()) if(!actor.isDeleted()) {
            final Sprite sprite = spriteShader.getSprite(actor.getSpriteName());
            spriteShader.paint(sprite, actor.getX(), actor.getY());
        }


        final List<Actor> removes = new ArrayList<>();
        for (final Alien alien: aliens) if (alien.isDeleted()) removes.add(alien);
        aliens.removeAll(removes);
        removes.clear();
        for (final Missile missile: missiles){
            if (missile.isDeleted()) removes.add(missile);
        }
        missiles.removeAll(removes);

    }

    @Override
    public void done() {

    }
}
