package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.messio.gl.FloatingPointMatrix.*;

/**
 * Created by jpc on 5/2/14.
 */
public class Text {
    private static final int FPS = 50;

    public static void test(FloatingPointMatrix pos, FloatingPointMatrix tex, int x, int y, char c){
        System.out.printf("Testing: (%s, %s) '%s'%n", x, y, c);
        for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++){
            System.out.printf(" Screen map (%s,%s) -> %s%n", i, j, pos.multiply(vec3f(x, y, 0).add(vec3f(i, j, 1))));
            System.out.printf(" Texture map (%s,%s) -> %s%n", i, j, tex.multiply(vec2f(c % 16, c / 16).add(vec2f(i, j))));
        }
    }

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Text");
        final GlContext context = new GlContext();
        System.out.println("OpenGL version: " + context.getVersion());
        final Program pgm = new Program.Builder()
                .vertexShaderFromResource("text_vs.glsl")
                .fragmentShaderFromResource("text_fs.glsl")
                .attribLocation(0, "in_Position")
                .uniformLocation("un_Screen")
                .uniformLocation("un_Texture")
                .uniformLocation("un_CharacterScreenPosition")
                .uniformLocation("un_CharacterTexturePosition")
                .build();
        final int charWidth = 8;
        final int charHeight = 8;
        final VertexArrayObject vao = new VertexArrayObject.Builder()
                .floatBuffer(0, vec2f(0, 0), vec2f(0, 1), vec2f(1, 1), vec2f(1, 0))
                .build();
        final BufferedImage bufferedImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("font_square_8x8_alpha.png"));

        final Texture texture = new Texture.Builder(TextureType.TWO_D)
                .texImage(bufferedImage)
                .build();

        context.setClearColor(0.4, 0.6, 0.9, 0);
        context.use(pgm);
        // matrix transforming a char coordinate to a texture position
        final FloatingPointMatrix tex = mat2f((double) charWidth / bufferedImage.getWidth(), 0, 0, (double) charHeight / bufferedImage.getHeight());
        pgm.uniformFloatingPointMatrix("un_Texture", false, tex);
        context.enable(Capability.BLEND).blendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
        Display.setResizable(true);
        boolean wasResized = true;
        while (!Display.isCloseRequested()){
            if (wasResized){
                int w = Display.getWidth();
                int h = Display.getHeight();
                context.viewPort(0, 0, w, h);
                final FloatingPointMatrix pos = mat3f(2.0 * charWidth / w, 0, 0, 0, -2.0 * charHeight / h, 0, -1, 1, 1);
                pgm.uniformFloatingPointMatrix("un_Screen", false, pos);
            }
            context.clear(Buffer.COLOR);
            context.bindTexture(texture);
            // matrix transforming a x,y char position to a normalized position
            int x = 0;
            for (char c: "Hello World!".toCharArray()){
                // System.out.println("pos: " + pos.multiply(vec3f(0, 1, 1)));
                final FloatingPointMatrix screenPos = vec2f(x++, 0);
                pgm.uniformFloatingPointMatrix("un_CharacterScreenPosition", false, screenPos);
                final FloatingPointMatrix texturePos = vec2f(c % (bufferedImage.getWidth() / charWidth), c / (bufferedImage.getHeight() / charHeight));
                pgm.uniformFloatingPointMatrix("un_CharacterTexturePosition", false, texturePos);
                context.drawArrays(DrawMode.TRIANGLE_FAN, vao);
            }
            Display.sync(FPS);
            Display.update();
            wasResized = Display.wasResized();
        }
        vao.close();
        Display.destroy();

    }
}
