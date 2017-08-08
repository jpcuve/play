package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.opengl.*;

import static com.messio.gl.FloatingPointMatrix.vec3f;
import static com.messio.gl.FloatingPointMatrix.vec4f;
import static com.messio.gl.IntegerMatrix.vec3i;

/**
 * Created by jpc on 4/29/14.
 */
public class FaceColor {
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

    private static class Triangle{
        public IntegerMatrix indices;
        public FloatingPointMatrix color;

        private Triangle(IntegerMatrix indices, FloatingPointMatrix color) {
            if (!(indices.isVec3() && color.isVec4())) throw new IllegalArgumentException();
            this.indices = indices;
            this.color = color;
        }
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
                .uniformLocation("un_Model")
                .uniformLocation("un_View")
                .uniformLocation("un_Projection")
                .build();
        final FloatingPointMatrix[] vertices = new FloatingPointMatrix[]{
                vec3f(0, 0, 0),
                vec3f(4, 4, 4),
                vec3f(3, 5, -2),
                vec3f(-3, 3, -2),
                vec3f(0, 14, 0),
                vec3f(0, 7, 3),
                vec3f(2, 7, 0),
                vec3f(0, 7, 0)
        };
        final Triangle[] triangles = new Triangle[]{
                new Triangle(vec3i(0, 6, 5), vec4f(1, 1, 1, 1)),
                new Triangle(vec3i(0, 7, 6), vec4f(1, 0, 0, 1)),
                new Triangle(vec3i(0, 5, 7), vec4f(0, 1, 0, 1)),
                new Triangle(vec3i(5, 6, 1), vec4f(0, 0, 1, 1)),
                new Triangle(vec3i(6, 7, 2), vec4f(1, 1, 1, 1)),
                new Triangle(vec3i(7, 5, 3), vec4f(1, 0, 1, 1)),
                new Triangle(vec3i(1, 4, 5), vec4f(1, 1, 0, 1)),
                new Triangle(vec3i(2, 4, 6), vec4f(0, 1, 1, 1)),
                new Triangle(vec3i(3, 5, 4), vec4f(1, 1, 1, 1)),
                new Triangle(vec3i(1, 6, 4), vec4f(1, 0, 0, 1)),
                new Triangle(vec3i(2, 7, 4), vec4f(0, 1, 0, 1)),
                new Triangle(vec3i(3, 4, 7), vec4f(0, 0, 1, 1)),

        };
        final FloatingPointMatrix[] positions = new FloatingPointMatrix[triangles.length * 3];
        final FloatingPointMatrix[] colors = new FloatingPointMatrix[triangles.length * 3];
        for (int i = 0; i < triangles.length; i++){
            final Triangle triangle = triangles[i];
            positions[i * 3] = vertices[triangle.indices.getX()];
            positions[i * 3 + 1] = vertices[triangle.indices.getY()];
            positions[i * 3 + 2] = vertices[triangle.indices.getZ()];
            colors[i * 3] = colors[i * 3 + 1] = colors[i * 3 + 2] = triangle.color;
        }
        try (final VertexArrayObject vao = new VertexArrayObject()){
            final FloatVertexBufferObject vboPositions = new FloatVertexBufferObject();
            vboPositions.buffer(positions);
            vao.setVertexBufferObject(0, vboPositions);
            final FloatVertexBufferObject vboColors = new FloatVertexBufferObject();
            vboColors.buffer(colors);
            vao.setVertexBufferObject(1, vboColors);


            context.enable(Capability.CULL_FACE, Capability.DEPTH_TEST, Capability.BLEND);
            GL11.glDepthRange(0, 1);
            context.setClearColor(0.4, 0.6, 0.9, 0);
            context.use(pgm);

            Display.setResizable(true);
            final FloatingPointMatrix lookAtViewMatrix = getLookAtViewMatrix(vec3f(0, 7, 10), vec3f(0, 0, 0), vec3f(0, 1, 0));
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
                context.drawArrays(DrawMode.TRIANGLES, vao);
                Display.sync(FPS);
                Display.update();
            }
        }
        Display.destroy();
    }
}
