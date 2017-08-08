package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.messio.gl.FloatingPointMatrix.vec2f;
import static com.messio.gl.FloatingPointMatrix.vec3f;
import static com.messio.gl.FloatingPointMatrix.vec4f;

/**
 * Created by jpc on 4/29/14.
 */
public class VirtualCamera {
    private static final int FPS = 50;

    public static FloatingPointMatrix getLookAtViewMatrix(FloatingPointMatrix eye, FloatingPointMatrix center, FloatingPointMatrix up){
        if (!eye.isVec3() || !center.isVec3() || !up.isVec3()) throw new IllegalArgumentException();
        final FloatingPointMatrix T = new FloatingPointMatrix(4, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -eye.getValue(0, 0), -eye.getValue(0, 1), -eye.getValue(0, 2), 1);
        final FloatingPointMatrix direction = center.subtract(eye);
        final FloatingPointMatrix f = direction.scalarDivide(direction.length());
        final FloatingPointMatrix u = up.scalarDivide(up.length());
        final FloatingPointMatrix r = f.cross(u);
        final FloatingPointMatrix R = new FloatingPointMatrix(4, r.getValue(0, 0), u.getValue(0, 0), -f.getValue(0, 0), 0, r.getValue(0, 1), u.getValue(0, 1), -f.getValue(0, 1), 0, r.getValue(0, 2), u.getValue(0, 2), -f.getValue(0, 2), 0, 0, 0, 0, 1);
        return R.multiply(T);
    }

    public static FloatingPointMatrix getPerspectiveProjectionMatrix(int width, int height, int fov, double near, double far){
        if (far <= near) throw new IllegalArgumentException();
        double sx = 2.0 * near * fov / width;
        double sy = 2.0 * near * fov / height;
        double sz = - (far + near) / (far - near);
        double pz = - 2.0 * far * near / (far - near);
        return new FloatingPointMatrix(4, sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, -1, 0, 0, pz, 0);
    }

    public static FloatingPointMatrix getOrthographicProjectionMatrix(int width, int height, double near, double far){
        if (far <= near) throw new IllegalArgumentException();
        double sx = 2.0 / width;
        double sy = 2.0 / height;
        double sz = - 2.0 / (far - near);
        double pz = - (far + near) / (far - near);
        return new FloatingPointMatrix(4, sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, -1, 0, 0, pz, 0);
    }

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Virtual camera");
        final GlContext context = new GlContext();
        System.out.println("OpenGL version: " + context.getVersion());
        final Program pgm = new Program.Builder()
                .vertexShaderFromResource("virtual_camera_vs.glsl")
                .fragmentShaderFromResource("virtual_camera_fs.glsl")
                .attribLocation(0, "in_Position")
                .attribLocation(1, "in_Color")
                .attribLocation(2, "in_TextureCoordinate")
                .uniformLocation("un_Model")
                .uniformLocation("un_View")
                .uniformLocation("un_Projection")
                .build();
        try (final VertexArrayObject vao = new VertexArrayObject()){
            final FloatVertexBufferObject vboPositions = new FloatVertexBufferObject();
            vboPositions.buffer(
                    vec3f(0, 0, 0),
                    vec3f(4, 4, 4),
                    vec3f(3, 5, -2),
                    vec3f(-3, 3, -2),
                    vec3f(0, 14, 0),
                    vec3f(0, 7, 3),
                    vec3f(2, 7, 0),
                    vec3f(0, 7, 0)
            );
            vao.setVertexBufferObject(0, vboPositions);
            final FloatVertexBufferObject vboColors = new FloatVertexBufferObject();
            vboColors.buffer(
                    vec4f(1, 1, 1, 1),
                    vec4f(1, 0, 0, 1),
                    vec4f(0, 1, 0, 1),
                    vec4f(0, 0, 1, 1),
                    vec4f(1, 1, 1, 1),
                    vec4f(1, 0, 1, 1),
                    vec4f(1, 1, 0, 1),
                    vec4f(0, 1, 1, 1)
            );
            vao.setVertexBufferObject(1, vboColors);

            final ByteVertexBufferObject vboi = new ByteVertexBufferObject();
            vboi.buffer(
                    0, 6, 5,
                    0, 7, 6,
                    0, 5, 7,
                    5, 6, 1,
                    6, 7, 2,
                    7, 5, 3,
                    1, 4, 5,
                    2, 4, 6,
                    3, 5, 4,
                    1, 6, 4,
                    2, 7, 4,
                    3, 4, 7
            );

            context.enable(Capability.CULL_FACE, Capability.DEPTH_TEST, Capability.BLEND);
            GL11.glDepthRange(0, 1);
            context.setClearColor(0.4, 0.6, 0.9, 0);
            context.use(pgm);

            Display.setResizable(true);
            final FloatingPointMatrix lookAtViewMatrix = getLookAtViewMatrix(vec3f(0, 0, 10), vec3f(0, 0, 0), vec3f(0, 1, 0));
            // final FloatingPointMatrix lookAtViewMatrix = FloatingPointMatrix.identity(4);
            System.out.println("look At: " + lookAtViewMatrix);
            pgm.uniformFloatingPointMatrix("un_View", false, lookAtViewMatrix);
            int angle = 0;
            while (!Display.isCloseRequested()){
                final FloatingPointMatrix perspectiveProjectionMatrix = getPerspectiveProjectionMatrix(Display.getWidth(), Display.getHeight(), 100, 2, 100);
                // final FloatingPointMatrix perspectiveProjectionMatrix = FloatingPointMatrix.identity(4);
                pgm.uniformFloatingPointMatrix("un_Projection", false, perspectiveProjectionMatrix);
                // System.out.println("perspective Projection: " + perspectiveProjectionMatrix);
                final FloatingPointMatrix model = FloatingPointMatrix.ry4f(angle);
                pgm.uniformFloatingPointMatrix("un_Model", false, model);
                if (angle == 0){
                    System.out.println("model: " + model);
                    System.out.println("perspective: " + perspectiveProjectionMatrix);
                    final FloatingPointMatrix point = vec4f(5, 5, 0, 1);
                    System.out.println("point transformation: " + perspectiveProjectionMatrix.multiply(lookAtViewMatrix.multiply(model.multiply(point))));
                }
                angle += 1;
                context.clear(Buffer.COLOR, Buffer.DEPTH);
                context.viewPort(0, 0, Display.getWidth(), Display.getHeight());
                context.drawElements(DrawMode.TRIANGLES, vao, vboi);
                Display.sync(FPS);
                Display.update();
            }
        }
        Display.destroy();
    }
}
