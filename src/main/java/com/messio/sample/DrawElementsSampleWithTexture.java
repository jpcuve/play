package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

import static com.messio.gl.FloatingPointMatrix.*;

/**
 * Created by jpc on 20/04/14.
 */
public class DrawElementsSampleWithTexture {
    private static final int FPS = 50;

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Input sample");
        final GlContext context = new GlContext();
        System.out.println("OpenGL version: " + context.getVersion());
        final Program pgm = new Program.Builder()
                .vertexShaderFromResource("draw_elements_sample_with_texture_vs.glsl")
                .fragmentShaderFromResource("draw_elements_sample_with_texture_fs.glsl")
                .attribLocation(0, "in_Position")
                .attribLocation(1, "in_Color")
                .attribLocation(2, "in_TextureCoordinate")
                .build();
        final VertexArrayObject vao = new VertexArrayObject.Builder()
                .floatBuffer(0, vec3f(-0.5, 0.5, 0.0),
                                vec3f(-0.5, -0.5, 0.0),
                                vec3f(0.5, -0.5, 0.0),
                                vec3f(0.5, 0.5, 0.0))
                .floatBuffer(2, vec2f(0.0, 0.0),
                                vec2f(0.0, 2),
                                vec2f(2, 2),
                                vec2f(2, 0.0))
                .build();

        final ByteVertexBufferObject vboi = new ByteVertexBufferObject();
        vboi.buffer(
                0, 1, 2,
                2, 3, 0
        );

        final URL resource = Thread.currentThread().getContextClassLoader().getResource("font_square_8x8_alpha.png");
        final BufferedImage bufferedImage = ImageIO.read(resource);
        final Texture texture = new Texture.Builder(TextureType.TWO_D)
                .texImage(bufferedImage)
                .build();

        context.setClearColor(0.4, 0.6, 0.9, 0);
        context.enable(Capability.BLEND);
        context.use(pgm);

        Display.setResizable(true);
        while (!Display.isCloseRequested()){
            context.clear(Buffer.COLOR);
            context.viewPort(0, 0, Display.getWidth(), Display.getHeight());
            context.bindTexture(texture);
            context.drawElements(DrawMode.TRIANGLES, vao, vboi);
            Display.sync(FPS);
            Display.update();
        }
        vao.close();
        Display.destroy();
    }
}
