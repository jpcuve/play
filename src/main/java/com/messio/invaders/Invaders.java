package com.messio.invaders;

import com.messio.gl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import javax.imageio.ImageIO;
import java.util.logging.Logger;

/**
 * Created by jpc on 04/06/14.
 */
public class Invaders {
    private static final Logger LOGGER = Logger.getLogger(Invaders.class.getCanonicalName());
    private static final int FPS = 50;
    public static final int GAME_WIDTH = 200;
    public static final int GAME_HEIGHT = 300;

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Invaders");
        final GlContext context = new GlContext();
        System.out.println("OpenGL version: " + context.getVersion());
        final SpriteShader spriteShader = new SpriteShader(context, "invaders/sprite.txt", GAME_WIDTH, GAME_HEIGHT);
        final FontShader fontShader = new FontShader(context, "invaders/font.txt", GAME_WIDTH, GAME_HEIGHT);
//        final Sprite background = new Sprite(ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("colors.png")));
        context.setClearColor(0.4, 0.6, 0.9, 0).enable(Capability.BLEND).blendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
        Display.setResizable(true);
        boolean wasResized = true;
        final Scene scene = new GameScene(GAME_WIDTH, GAME_HEIGHT, fontShader, spriteShader);
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            if (wasResized){
                int w = Display.getWidth();
                int h = Display.getHeight();
                context.viewPort(0, 0, w, h);
                spriteShader.setScreenTransform(w, h);
                fontShader.setScreenTransform(w, h);
            }
            context.clear(Buffer.COLOR);

            // spriteShader.paint(background, 0, 0, GAME_WIDTH, GAME_HEIGHT, 0);
            final Input input = new Input(Keyboard.isKeyDown(Keyboard.KEY_LEFT), Keyboard.isKeyDown(Keyboard.KEY_RIGHT), Keyboard.isKeyDown(Keyboard.KEY_SPACE));
            scene.pulse(input);
            scene.paint();


            Display.sync(FPS);
            Display.update();
            wasResized = Display.wasResized();
        }
        spriteShader.close();
        Display.destroy();

    }
}
