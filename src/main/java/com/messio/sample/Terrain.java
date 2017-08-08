package com.messio.sample;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Created by jpc on 5/5/14.
 */
public class Terrain {
    private final Random random = new Random();
    private final double[][] heights;
    private double min;
    private double max;

    public Terrain(int edge) {
        heights = new double[edge][edge];
        double roughness = random.nextDouble();
        double base = edge;
        for (int dim = edge; dim > 1; dim /= 2){

//            System.out.printf("pass: %s, base: %s%n", dim, base);
            // diamond
            int half = dim / 2;
            for (int i = 0; i < edge; i += dim) for (int j = 0; j < edge; j += dim) {
                int xc = i + half;
                int yc = j + half;
                int r = i + dim;
                if (r >= edge) r -= edge;
                int b = j + dim;
                if (b >= edge) b -= edge;
//                System.out.printf("computing center: (%s, %s)%n", xc, yc);
                double height = (heights[i][j] + heights[r][j] + heights[i][b] + heights[r][b]) / 4;
                height += getPerturbation(base);
                if (height < min) min = height;
                if (height > max) max = height;
                heights[xc][yc] = height;
            }
            // square
            for (int i = 0; i < edge; i += half) for (int j = 0; j < edge; j+= half) {
                boolean xb = ((i / half) % 2) == 0;
                boolean yb = ((j / half) % 2) == 0;
                if ((xb && !yb) || (!xb && yb)){
//                    System.out.printf("computing edge: (%s, %s)%n", i, j);
                    int l = i - half;
                    if (l < 0) l += edge;
                    int r = i + half;
                    if (r >= edge) r -= edge;
                    int t = j - half;
                    if (t < 0) t += edge;
                    int b = j + half;
                    if (b >= edge) b -= edge;
                    double height = (heights[l][j] + heights[r][j] + heights[i][t] + heights[i][b]) / 4;
                    height += getPerturbation(base);
                    if (height < min) min = height;
                    if (height > max) max = height;
                    heights[i][j] = height;
                }
            }
            base /= 4;
        }
    }

    public double[][] getHeights() {
        return heights;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    private double getPerturbation(double scale){
        return scale * (1 - 2 * random.nextDouble());
    }

    public static void main(String[] args) {
        int edge = 256;
        final Terrain terrain = new Terrain(edge);
        System.out.printf("edge: %s, min: %s, max: %s%n", edge, terrain.getMin(), terrain.getMax());
        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth();
                int h = getHeight();
                g.drawLine(0, 0, w, h);
                double[][] v = terrain.getHeights();
                for (int i = 0; i < edge; i++) for (int j = 0; j < edge; j++) {
                    int blue = (int) ((v[i][j] - terrain.getMin()) * 255 / (terrain.getMax() - terrain.getMin()));
                    Color color = new Color(0, 0, blue);
                    g.setColor(color);
                    g.drawLine(i, j, i + 1, j + 1);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(edge, edge);
            }
        });
        f.pack();
        f.setVisible(true);

    }
}
