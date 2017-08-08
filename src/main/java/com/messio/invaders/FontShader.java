package com.messio.invaders;

import com.messio.gl.*;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.messio.gl.FloatingPointMatrix.*;

/**
 * Created by jpc on 04/06/14.
 */
public class FontShader implements Closeable {
    private static final String UNIFORM_SCALE = "un_Scale";
    private static final String UNIFORM_SCREEN = "un_Screen";
    private static final String UNIFORM_CHAR_SIZE = "un_CharSize";
    private static final String UNIFORM_CHAR_SCREEN_POSITION = "un_CharScreenPosition";
    private static final String UNIFORM_OFFSET = "un_Offset";
    private static final String UNIFORM_TOTAL = "un_Total";
    private final GlContext context;
    private final int gameWidth;
    private final int gameHeight;
    private final VertexArrayObject vao;
    private final Program pgm;
    private final Texture texture;
    private final int fontHeight;
    private final List<Integer> widths;
    private final List<Integer> offsets;

    public FontShader(final GlContext context, final String resourceName, int gameWidth, int gameHeight){
        this.context = context;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.vao = new VertexArrayObject.Builder()
                .floatBuffer(0, vec3f(0, 0, 1), vec3f(0, 1, 1), vec3f(1, 1, 1), vec3f(1, 0, 1))
                .build();
        this.pgm = new Program.Builder()
                .vertexShaderFromResource("invaders/font_vs.glsl")
                .fragmentShaderFromResource("invaders/font_fs.glsl")
                .attribLocation(0, "in_Position")
                .uniformLocations(UNIFORM_SCREEN, UNIFORM_CHAR_SIZE, UNIFORM_CHAR_SCREEN_POSITION, UNIFORM_SCALE, UNIFORM_OFFSET, UNIFORM_TOTAL)
                .build();
        // read font texture
        final Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        this.widths = new ArrayList<>();
        this.offsets = new ArrayList<>();
        this.fontHeight = scanner.nextInt();
        final StringBuilder[] sbs = new StringBuilder[fontHeight];
        int characterCountPerLine = scanner.nextInt();
        int lineCount = scanner.nextInt();
        int offset = 0;
        for (int i = 0; i < lineCount; i++){
            for (int j = 0; j < fontHeight; j++){
                if (sbs[j] == null) sbs[j] = new StringBuilder();
                for (int k = 0; k < characterCountPerLine; k++){
                    final String s = scanner.next();
                    sbs[j].append(s);
                    if (j == 0){
                        widths.add(s.length());
                        offsets.add(offset);
                        offset += s.length();
                    }
                }
            }
        }
        int width = sbs[0].length();
        final BufferedImage image = new BufferedImage(width, fontHeight, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < fontHeight; row++) for (int col = 0; col < sbs[row].length(); col++) image.setRGB(col, sbs.length - 1 - row, sbs[row].charAt(col) != '.' ? 0xFFFFFFFF : 0);
        this.texture = new Texture.Builder(TextureType.TWO_D).texImage(image).build();
        context.use(pgm);
        pgm.uniform(UNIFORM_TOTAL, width);
        System.out.println("stop");
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
    }

    public void paint(char c, int x, int y, double scale){
        context.use(pgm);
        pgm.uniform(UNIFORM_SCALE, scale).uniform(UNIFORM_OFFSET, offsets.get(c)).uniformFloatingPointMatrix(UNIFORM_CHAR_SIZE, vec2f(widths.get(c), fontHeight)).uniformFloatingPointMatrix(UNIFORM_CHAR_SCREEN_POSITION, vec2f(x, y));
        context.bindTexture(texture);
        context.drawArrays(DrawMode.TRIANGLE_FAN, vao);
    }

    public int strlen(final String s){
        if (s == null || s.length() == 0) return 0;
        int len = 0;
        for (char c: s.toCharArray()) len += widths.get(c);
        return len + s.length() - 1;
    }

    public void paint(final String s, int x, int y, double scale){
        int off = x;
        if (s != null) for (char c: s.toCharArray()){
            paint(c, off, y, scale);
            off += (widths.get(c) + 1) * scale;
        }
    }

    @Override
    public void close() throws IOException {
        pgm.close();
        vao.close();
    }
}
