package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.opengl.*;

/**
 * Created by jpc on 20/04/14.
 */
public class DrawElementsSample {
    private static final int FPS = 50;

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Input sample");
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        final Program pgm = new Program.Builder().vertexShaderFromResource("input_sample_vs.glsl").fragmentShaderFromResource("input_sample_fs.glsl").build();
        try (final VertexArrayObject vao = new VertexArrayObject()){
            final double points[] = {
                    -0.5, 0.5, 0.0,
                    -0.5, -0.5, 0.0,
                    0.5, -0.5, 0.0,
                    0.5, 0.5, 0.0
            };
            final FloatVertexBufferObject vbo = new FloatVertexBufferObject();
            vbo.bufferData(3, points);
            vao.setVertexBufferObject(0, vbo);
            final int[] indices = {
                    0, 1, 2,
                    2, 3, 0
            };
            final ByteVertexBufferObject vboi = new ByteVertexBufferObject();
            vboi.bufferData(1, indices);

            final GlContext context = new GlContext();
            context.setClearColor(0.4f, 0.6f, 0.9f, 0f);
            context.use(pgm);

            while (!Display.isCloseRequested()){
                context.clear(Buffer.COLOR);
                context.drawElements(DrawMode.TRIANGLES, vao, vboi);
                Display.sync(FPS);
                Display.update();
            }
        }
        Display.destroy();
    }
}
