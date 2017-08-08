package com.messio.sample;

import com.messio.gl.*;
import com.messio.gl.wavefront.DefaultWavefrontHandler;
import com.messio.gl.wavefront.WavefrontParser;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import static com.messio.gl.FloatingPointMatrix.*;

/**
 * Created by jpc on 4/29/14.
 */
public class Rocket {
    private static final int FPS = 50;

    public static void main(String[] args) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.setTitle("Rocket");
        final GlContext context = new GlContext();
        System.out.println("OpenGL version: " + context.getVersion());

        // sentinel static stuff
        final Program pgmRocket = new Program.Builder()
                .vertexShaderFromResource("rocket_vs.glsl")
                .fragmentShaderFromResource("rocket_fs.glsl")
                .attribLocation(0, "in_Position")
                .attribLocation(1, "in_Color")
                .uniformLocation("un_Model")
                .uniformLocation("un_View")
                .uniformLocation("un_Projection")
                .build();
        final DefaultWavefrontHandler defaultWavefrontHandler = new DefaultWavefrontHandler();
        new WavefrontParser(defaultWavefrontHandler).parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("geodome.obj"));
        final VertexArrayObject vaoRocket = new VertexArrayObject.Builder()
                .floatBuffer(0, defaultWavefrontHandler.getTriangleVertexes())
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
        int edge = 4;
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
        while (!Display.isCloseRequested()){
            final FloatingPointMatrix eye = ry4f(angleEyeY).multiply(rz4f(angleEyeZ).multiply(vec4f(distEye, 0, 0, 1))).extract(-1, 3);
//            System.out.printf("length: %s, eye: %s, l: %s%n", multiply.length(), eye, eye.length());
//            final FloatingPointMatrix lookAtViewMatrix = getLookAtViewMatrix(eye, vec3f(0, 0, 0), up);
            // System.out.println("look At: " + lookAtViewMatrix);
            if (Mouse.isGrabbed()){
                int dx = Mouse.getDX();
                int dy = Mouse.getDY();
                yaw -= dx;
                pitch += dy;
                int dw = Mouse.getDWheel() / 100;
                distEye += dw;
                if (distEye < 1) distEye = 1;
            }
            while (Keyboard.next()){
                switch (Keyboard.getEventKey()){
                    case Keyboard.KEY_E:
                        if (Keyboard.getEventKeyState()) Mouse.setGrabbed(!Mouse.isGrabbed());
                        break;
                }
            }
            if (pitch > Angle.UNITS_PER_HALF_PI) pitch = Angle.UNITS_PER_HALF_PI;
            if (pitch < -Angle.UNITS_PER_HALF_PI) pitch = -Angle.UNITS_PER_HALF_PI;
            final FloatingPointMatrix view = firstPerson(eye, pitch, yaw);

            context.clear(Buffer.COLOR, Buffer.DEPTH);
            context.viewPort(0, 0, Display.getWidth(), Display.getHeight());
            final FloatingPointMatrix projectionMatrix = perspectiveProjection(Display.getWidth(), Display.getHeight(), 100, 2, 100);

            // terrain
            context.polygonMode(PolygonMode.LINE);
            context.use(pgmTerrain);
            pgmTerrain.uniformFloatingPointMatrix("un_View", false, view);
            pgmTerrain.uniformFloatingPointMatrix("un_Projection", false, projectionMatrix);
            for (final VertexArrayObject vao: vaos){
                context.drawArrays(DrawMode.TRIANGLE_STRIP, vao);
            }

            // sentinel
            context.polygonMode(PolygonMode.LINE);
            context.use(pgmRocket);
            final FloatingPointMatrix model = FloatingPointMatrix.ry4f(angleSentinel);
            pgmRocket.uniformFloatingPointMatrix("un_Model", false, model);
            pgmRocket.uniformFloatingPointMatrix("un_View", false, view);
            pgmRocket.uniformFloatingPointMatrix("un_Projection", false, projectionMatrix);
            context.drawArrays(DrawMode.TRIANGLES, vaoRocket);

            double eyeX = eye.getX();
            double eyeY = eye.getY();
/*
            if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) distEye++;
            if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) distEye--;
*/
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) angleEyeY--;
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) angleEyeY++;
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) angleEyeZ++;
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) angleEyeZ--;

//            eye = vec3f(eyeX, eyeY, eye.getZ());

            Display.sync(FPS);
            Display.update();
            angleSentinel -= 1;
        }
        vaoRocket.close();
        Display.destroy();

    }
}
