package com.messio.invaders;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpc on 6/17/14.
 */
public class Bunker {
    private final List<Block> blocks = new ArrayList<>();
    protected int x;
    protected int y;

    public Bunker(int x, int y, final Dimension blockDimension) {
        this.x = x;
        this.y = y;
        for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++){
            final Block block = new Block(x + i * blockDimension.width, y + j * blockDimension.height);
            blocks.add(block);
        }
    }

    public List<Block> getBlocks(){
        return blocks;
    }
}
