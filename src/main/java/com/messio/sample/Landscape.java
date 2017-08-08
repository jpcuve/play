package com.messio.sample;

import com.messio.gl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

import static com.messio.gl.FloatingPointMatrix.vec3f;
import static com.messio.gl.FloatingPointMatrix.vec4f;
import static com.messio.gl.IntegerMatrix.vec3i;

import static com.messio.gl.Angle.*;

/**
 * Created by jpc on 4/29/14.
 */
public class Landscape {
    private static final int FPS = 50;

    public static FloatingPointMatrix getLookAtViewMatrix(FloatingPointMatrix eye, FloatingPointMatrix center, FloatingPointMatrix up){
        if (!eye.isVec3() || !center.isVec3() || !up.isVec3()) throw new IllegalArgumentException();
        final FloatingPointMatrix z = eye.subtract(center).normalize();
        final FloatingPointMatrix x = up.cross(z).normalize();
        final FloatingPointMatrix y = z.cross(x);
        final FloatingPointMatrix orientation = new FloatingPointMatrix(4, x.getValue(0, 0), y.getValue(0, 0), z.getValue(0, 0), 0, x.getValue(0, 1), y.getValue(0, 1), z.getValue(0, 1), 0, x.getValue(0, 2), y.getValue(0, 2), z.getValue(0, 2), 0, 0, 0, 0, 1);
        final FloatingPointMatrix translation = new FloatingPointMatrix(4, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -eye.getValue(0, 0), -eye.getValue(0, 1), -eye.getValue(0, 2), 1);
        return orientation.multiply(translation);
    }

    public static FloatingPointMatrix getFirstPersonViewMatrix(FloatingPointMatrix eye, int pitch, int yaw){
        if (pitch < -Angle.UNITS_PER_HALF_PI || pitch > Angle.UNITS_PER_HALF_PI) throw new IllegalArgumentException();
        final FloatingPointMatrix x = vec3f(cos(yaw), 0, -sin(yaw));
        final FloatingPointMatrix y = vec3f(sin(yaw) * sin(pitch), cos(pitch), cos(yaw) * sin(pitch));
        final FloatingPointMatrix z = vec3f(sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch));
        return new FloatingPointMatrix(4, x.getX(), y.getX(), z.getX(), 0, x.getY(), y.getY(), z.getY(), 0, x.getZ(), y.getZ(), z.getZ(), 0, -x.dot(eye), - y.dot(eye), -z.dot(eye), 1);
    }

    public static FloatingPointMatrix getPerspectiveProjectionMatrix(int width, int height, int fov, double near, double far){
        if (far <= near) throw new IllegalArgumentException();
        double sx = 2.0 * near * fov / width;
        double sy = 2.0 * near * fov / height;
        double sz = - (far + near) / (far - near);
        double pz = - 2.0 * far * near / (far - near);
        return new FloatingPointMatrix(4, sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, -1, 0, 0, pz, 0);
    }

    public static FloatingPointMatrix getOrthographicProjectionMatrix(int width, int height, int fov, double near, double far){
        if (far <= near) throw new IllegalArgumentException();
        double sx = 2.0 * fov / width;
        double sy = 2.0 * fov / height;
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

        // sentinel static stuff
        final Program pgmSentinel = new Program.Builder()
                .vertexShaderFromResource("landscape_sentinel_vs.glsl")
                .fragmentShaderFromResource("landscape_sentinel_fs.glsl")
                .attribLocation(0, "in_Position")
                .attribLocation(1, "in_Color")
                .uniformLocation("un_Model")
                .uniformLocation("un_View")
                .uniformLocation("un_Projection")
                .build();
        final FloatingPointMatrix[] verticesSentinel = new FloatingPointMatrix[]{
                vec3f(0, 0, 0),
                vec3f(4, 4, 4),
                vec3f(3, 5, -2),
                vec3f(-3, 3, -2),
                vec3f(0, 14, 0),
                vec3f(0, 7, 3),
                vec3f(2, 7, 0),
                vec3f(0, 7, 0)
        };
        final Triangle[] trianglesSentinel = new Triangle[]{
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
        final FloatingPointMatrix[] positions = new FloatingPointMatrix[trianglesSentinel.length * 3];
        final FloatingPointMatrix[] colors = new FloatingPointMatrix[trianglesSentinel.length * 3];
        for (int i = 0; i < trianglesSentinel.length; i++){
            final Triangle triangle = trianglesSentinel[i];
            positions[i * 3] = verticesSentinel[triangle.indices.getX()];
            positions[i * 3 + 1] = verticesSentinel[triangle.indices.getY()];
            positions[i * 3 + 2] = verticesSentinel[triangle.indices.getZ()];
            colors[i * 3] = colors[i * 3 + 1] = colors[i * 3 + 2] = triangle.color;
        }
        final VertexArrayObject vaoSentinel = new VertexArrayObject.Builder()
                .floatBuffer(0, positions)
                .floatBuffer(1, colors)
                .build();

        // terrain static stuff
        final Program pgmTerrain = new Program.Builder()
                .vertexShaderFromResource("landscape_terrain_vs.glsl")
                .fragmentShaderFromResource("landscape_terrain_fs.glsl")
                .attribLocation(0, "in_Position")
                .uniformLocation("un_Model")
                .uniformLocation("un_View")
                .uniformLocation("un_Projection")
                .build();
        int edge = 64;
        int half = edge / 2;
        final Terrain terrain = new Terrain(edge);
        double max = terrain.getMax();
        double min = terrain.getMin();
        double factor = 30.0 / (max - min); // set amplitude of terrain to y = 14 max around 0, eye is at y = 7
        final double[][] heights = terrain.getHeights();
        final VertexArrayObject[] vaos = new VertexArrayObject[edge - 1];
        for (int j = 0; j < edge - 1; j++){
            final FloatingPointMatrix[] stripVertexes = new FloatingPointMatrix[edge * 2];
            double squareEdge = 10;
            for (int i = 0; i < edge; i++){
                stripVertexes[i * 2] = vec3f((i - half) * squareEdge, heights[i][j] * factor, (j - half) * squareEdge);
                stripVertexes[i * 2 + 1] = vec3f((i - half) * squareEdge, heights[i][j + 1] * factor, (j + 1 - half) * squareEdge);
            }
            final VertexArrayObject vaoStrip = new VertexArrayObject.Builder()
                    .floatBuffer(0, stripVertexes)
                    .build();
            vaos[j] = vaoStrip;
        }

        context.enable(Capability.CULL_FACE, Capability.DEPTH_TEST, Capability.BLEND);
        context.depthRange(0, 1);
        context.setClearColor(0.4, 0.6, 0.9, 0);

        Display.setResizable(true);
        // FloatingPointMatrix eye = vec3f(0, 20, 10);
        FloatingPointMatrix up = vec3f(0, 1, 0);
        int angleSentinel = 0;
        int angleEyeZ = Angle.UNITS_PER_HALF_PI / 2;
        int angleEyeY = 0;
        int distEye = 20;
        int pitch = 0;
        int yaw = 0;
        Mouse.setGrabbed(true);
        while (!Display.isCloseRequested()){
            final FloatingPointMatrix eye = FloatingPointMatrix.ry4f(angleEyeY).multiply(FloatingPointMatrix.rz4f(angleEyeZ).multiply(vec4f(distEye, 0, 0, 1))).extract(-1, 3);
//            System.out.printf("length: %s, eye: %s, l: %s%n", multiply.length(), eye, eye.length());
//            final FloatingPointMatrix lookAtViewMatrix = getLookAtViewMatrix(eye, vec3f(0, 0, 0), up);
            // System.out.println("look At: " + lookAtViewMatrix);
            if (Mouse.isGrabbed()){
                int dx = Mouse.getDX();
                int dy = Mouse.getDY();
                yaw -= dx;
                pitch += dy;
            }
            while (Mouse.next()){
                switch (Mouse.getEventButton()){
                    case 1:
                        if (Mouse.getEventButtonState()) Mouse.setGrabbed(!Mouse.isGrabbed());
                        break;
                }
            }
            if (pitch > Angle.UNITS_PER_HALF_PI) pitch = Angle.UNITS_PER_HALF_PI;
            if (pitch < -Angle.UNITS_PER_HALF_PI) pitch = -Angle.UNITS_PER_HALF_PI;
            final FloatingPointMatrix view = getFirstPersonViewMatrix(eye, pitch, yaw);

            context.clear(Buffer.COLOR, Buffer.DEPTH);
            context.viewPort(0, 0, Display.getWidth(), Display.getHeight());
            final FloatingPointMatrix projectionMatrix = getPerspectiveProjectionMatrix(Display.getWidth(), Display.getHeight(), 100, 2, 100);

            // terrain
            context.polygonMode(PolygonMode.LINE);
            context.use(pgmTerrain);
            pgmTerrain.uniformFloatingPointMatrix("un_View", false, view);
            pgmTerrain.uniformFloatingPointMatrix("un_Projection", false, projectionMatrix);
            for (final VertexArrayObject vao: vaos){
                context.drawArrays(DrawMode.TRIANGLE_STRIP, vao);
            }

            // sentinel
            context.polygonMode(PolygonMode.FILL);
            context.use(pgmSentinel);
            final FloatingPointMatrix model = FloatingPointMatrix.ry4f(angleSentinel);
            pgmSentinel.uniformFloatingPointMatrix("un_Model", false, model);
            pgmSentinel.uniformFloatingPointMatrix("un_View", false, view);
            pgmSentinel.uniformFloatingPointMatrix("un_Projection", false, projectionMatrix);
            context.drawArrays(DrawMode.TRIANGLES, vaoSentinel);

            double eyeX = eye.getX();
            double eyeY = eye.getY();
            if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) distEye++;
            if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) distEye--;
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) angleEyeY--;
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) angleEyeY++;
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) angleEyeZ++;
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) angleEyeZ--;

            if (distEye < 5) distEye = 5;
//            eye = vec3f(eyeX, eyeY, eye.getZ());

            Display.sync(FPS);
            Display.update();
            angleSentinel -= 1;
        }
        vaoSentinel.close();
        Display.destroy();
    }
}
