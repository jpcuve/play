package com.messio.gl;

import javax.swing.*;
import java.awt.*;

import static com.messio.gl.FloatingPointMatrix.vec3f;

/**
 * Created by jpc on 4/28/14.
 */
public class Angle {
    public static final int UNITS_PER_HALF_PI = 512;
    public static final int UNITS_PER_PI = 2 * UNITS_PER_HALF_PI;
    public static final int UNITS_PER_THREE_HALF_PI = 3 * UNITS_PER_HALF_PI;
    public static final int UNITS_PER_TWO_PI = 4 * UNITS_PER_HALF_PI;
    private static final double[] values = new double[UNITS_PER_HALF_PI + 1];

    static {
        for (int i = 0; i < UNITS_PER_HALF_PI; i++) values[i] = (float) Math.sin(i * Math.PI / UNITS_PER_HALF_PI / 2);
        values[UNITS_PER_HALF_PI] = 1.0;
    }

    public static double sin(int units){
        if (units < 0) units = units % UNITS_PER_TWO_PI + UNITS_PER_TWO_PI;
        int base = units % UNITS_PER_HALF_PI;
        int quadrant = (units % UNITS_PER_TWO_PI) / UNITS_PER_HALF_PI;
        switch (quadrant){
            case 0: return values[base];
            case 1: return values[UNITS_PER_HALF_PI - base];
            case 2: return -values[base];
            case 3: return -values[UNITS_PER_HALF_PI - base];
        }
        throw new IllegalArgumentException();
    }

    public static double cos(int units){
        if (units < 0) units = units % UNITS_PER_TWO_PI + UNITS_PER_TWO_PI;
        int base = units % UNITS_PER_HALF_PI;
        int quadrant = (units % UNITS_PER_TWO_PI) / UNITS_PER_HALF_PI;
        switch (quadrant){
            case 0: return values[UNITS_PER_HALF_PI - base];
            case 1: return -values[base];
            case 2: return -values[UNITS_PER_HALF_PI - base];
            case 3: return values[base];
        }
        throw new IllegalArgumentException();
    }

    public static double tan(int units){
        if (units % UNITS_PER_PI == 0) throw new IllegalArgumentException();
        return sin(units) / cos(units);
    }

    public static void main(String[] args) {
        final JFrame f = new JFrame();
        final Dimension d = new Dimension(2 * UNITS_PER_TWO_PI, UNITS_PER_TWO_PI);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setContentPane(new JPanel() {
            @Override
            public void paint(Graphics g) {
                final int width = getWidth();
                final int height = getHeight();
                for (int i = -UNITS_PER_TWO_PI; i < UNITS_PER_TWO_PI; i++) {
                    int x = (i * width) / (2 * UNITS_PER_TWO_PI);
                    int y1 = (int) (height * sin(i) / 2);
                    g.drawLine(x, height / 2 - y1, x, height / 2 - y1 + 2);
                    int y2 = (int) (height * cos(i) / 2);
                    g.drawLine(x, height / 2 - y2, x + 2, height / 2 - y2);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return d;
            }
        });
        f.pack();
        f.setVisible(true);
    }
}
