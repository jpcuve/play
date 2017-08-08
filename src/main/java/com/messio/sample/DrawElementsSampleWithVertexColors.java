package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.opengl.*;
import static com.messio.gl.FloatingPointMatrix.*;

/**
 * Created by jpc on 20/04/14.
 */
public class DrawElementsSampleWithVertexColors {
    private static final int FPS = 50;

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(600, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Input sample");
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        final Program pgm = new Program.Builder()
                .vertexShaderFromResource("draw_elements_sample_with_vertex_color_vs.glsl")
                .fragmentShaderFromResource("draw_elements_sample_with_vertex_color_fs.glsl")
                .uniformLocation("matrix")
                .build();
        try (final VertexArrayObject vao = new VertexArrayObject()){
            final FloatVertexBufferObject vboPositions = new FloatVertexBufferObject();
            vboPositions.buffer(
                    vec3f(-0.5, 0.5, 0),
                    vec3f(-0.5, -0.5, 0),
                    vec3f(0.5, -0.5, 0),
                    vec3f(0.5, 0.5, 0)
            );
            vao.setVertexBufferObject(0, vboPositions);
            final FloatVertexBufferObject vboColors = new FloatVertexBufferObject();
            vboColors.buffer(
                    vec4f(1.0, 0.0, 0.0, 1.0),
                    vec4f(0.0, 1.0, 0.0, 1.0),
                    vec4f(0.0, 0.0, 1.0, 1.0),
                    vec4f(1.0, 1.0, 1.0, 1.0)
            );
            vao.setVertexBufferObject(1, vboColors);

            final ByteVertexBufferObject vboi = new ByteVertexBufferObject();
            vboi.buffer(0, 1, 2, 2, 3, 0);

            final GlContext context = new GlContext();
            context.setClearColor(0.4, 0.6, 0.9, 0);
            context.use(pgm);

            int angle = 0;
            while (!Display.isCloseRequested()){
                double translation = Angle.sin(angle);
                pgm.uniformFloatingPointMatrix("matrix", false, FloatingPointMatrix.rz4f(angle++).multiply(FloatingPointMatrix.translation4f(0.5, 0, 0)));
                context.clear(Buffer.COLOR);
                context.drawElements(DrawMode.TRIANGLES, vao, vboi);
                Display.sync(FPS);
                Display.update();
            }
        }
        Display.destroy();
    }
}
