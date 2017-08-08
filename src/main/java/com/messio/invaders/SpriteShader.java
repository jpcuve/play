package com.messio.invaders;

import com.messio.gl.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.messio.gl.FloatingPointMatrix.mat3f;
import static com.messio.gl.FloatingPointMatrix.vec2f;
import static com.messio.gl.FloatingPointMatrix.vec3f;

/**
 * Created by jpc on 04/06/14.
 */
public class SpriteShader implements Closeable {
    private static final String UNIFORM_SCREEN = "un_Screen";
    private static final String UNIFORM_SPRITE_SIZE = "un_SpriteSize";
    private static final String UNIFORM_SPRITE_SCREEN_POSITION = "un_SpriteScreenPosition";
    private static final String UNIFORM_ROTATION = "un_Rotation";
    private final GlContext context;
    private final int gameWidth;
    private final int gameHeight;
    private final VertexArrayObject vao;
    private final Program pgm;
    private final Map<String, Sprite> spriteMap = new HashMap<>();

    public SpriteShader(final GlContext context, final String resourceName, int gameWidth, int gameHeight){
        this.context = context;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.vao = new VertexArrayObject.Builder()
                .floatBuffer(0, vec3f(0, 0, 1), vec3f(0, 1, 1), vec3f(1, 1, 1), vec3f(1, 0, 1))
                .build();
        this.pgm = new Program.Builder()
                .vertexShaderFromResource("invaders/sprite_vs.glsl")
                .fragmentShaderFromResource("invaders/sprite_fs.glsl")
                .attribLocation(0, "in_Position")
                .uniformLocations(UNIFORM_SCREEN, UNIFORM_SPRITE_SIZE, UNIFORM_SPRITE_SCREEN_POSITION, UNIFORM_ROTATION)
                .build();
        final Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        int spriteCount = scanner.nextInt();
        for (int i = 0; i < spriteCount; i++){
            final String name = scanner.next();
            System.out.printf("reading: %s%n", name);
            final int height = scanner.nextInt();
            BufferedImage[] images = null;
            int width = 0;
            scanner.nextLine();
            for (int j = 0; j < height; j++){
                final String line = scanner.nextLine();
                final String[] ss = line.split(" ");
                if (j == 0){
                    images = new BufferedImage[ss.length];
                    if (ss.length > 0) {
                        width = ss[0].length();
                        for (int k = 0; k < ss.length; k++) images[k] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    }
                }
                for (int im = 0; im < images.length; im++){
                    final String s = ss[im];
                    for (int l = 0; l < width; l++){
                        int color = 0x00000000;
                        switch(s.charAt(l)){
                            case 'O':
                                color = 0xFFFFFFFF;
                                break;
                        }
                        images[im].setRGB(l, height - j - 1, color);
                    }
                }
            }
            if (images != null){
                for (int im = 0; im < images.length; im++){
                    final Sprite sprite = new Sprite(images[im]);
                    final String spriteName = images.length < 2 ? name : String.format("%s:%d", name, im);
                    System.out.printf("  storing: %s%n", spriteName);
                    spriteMap.put(spriteName, sprite);
                }
            }
        }
    }

    public void setScreenTransform(int viewportWidth, int viewportHeight){
        double xfactor;
        double yfactor;
        final double gameAspectRatio = (double) gameWidth / gameHeight;
        final double screenAspectRatio = (double) viewportWidth / viewportHeight;
        if (gameAspectRatio < screenAspectRatio){
            System.out.printf("case 1: %s %s%n", gameAspectRatio, screenAspectRatio);
            xfactor = 2.0 * viewportHeight / viewportWidth / gameHeight;
            yfactor = 2.0 / gameHeight;
        } else{
            System.out.printf("case 2: %s %s%n", gameAspectRatio, screenAspectRatio);
            xfactor = 2.0 / gameWidth;
            yfactor = 2.0 * viewportWidth / viewportHeight / gameWidth;
        }
        System.out.printf("xf: %s, yf: %s%n", xfactor, yfactor);
        final FloatingPointMatrix pos = mat3f(xfactor, 0, 0, 0, yfactor, 0, 0, 0, 1);
        context.use(pgm);
        pgm.uniformFloatingPointMatrix(UNIFORM_SCREEN, pos);
        final FloatingPointMatrix spr = mat3f(gameWidth, 0, 0, 0, gameHeight, 0, -gameWidth / 2, - gameHeight / 2, 1);
        System.out.printf(" lt: %s%n", pos.multiply(spr.multiply(vec3f(0, 0, 1))));
        System.out.printf(" rb: %s%n", pos.multiply(spr.multiply(vec3f(1, 1, 1))));
    }

    public void paint(Sprite sprite, int x, int y, int width, int height, double rotation){
        context.use(pgm);
        pgm.uniform(UNIFORM_ROTATION, rotation).uniformFloatingPointMatrix(UNIFORM_SPRITE_SIZE, vec2f(width, height)).uniformFloatingPointMatrix(UNIFORM_SPRITE_SCREEN_POSITION, vec2f(x, y));
        context.bindTexture(sprite.getTexture());
        context.drawArrays(DrawMode.TRIANGLE_FAN, vao);
    }

    public void paint(Sprite sprite, int x, int y){
        paint(sprite, x, y, sprite.getWidth(), sprite.getHeight(), 0);
    }

    public void paint(Sprite sprite, int x, int y, double rotation){
        paint(sprite, x, y, sprite.getWidth(), sprite.getHeight(), rotation);
    }

    public Sprite getSprite(final String spriteName){
        return spriteMap.get(spriteName);
    }

    public Dimension getSpriteSize(final String spriteName){ // should depend on magnification and rotation...
        final Sprite sprite = getSprite(spriteName);
        return sprite == null ? null : new Dimension(sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void close() throws IOException {
        pgm.close();
        vao.close();
    }
}
