package com.messio.gl;

import org.junit.Test;
import static org.junit.Assert.*;

import static com.messio.gl.FloatingPointMatrix.*;


/**
 * Created by jpc on 4/25/14.
 */
public class MatrixTest {
    private final FloatingPointMatrix m1 = new FloatingPointMatrix(3, 1f, 2f, 3f, 5f, 6f, 7f);
    private final FloatingPointMatrix v1 = new FloatingPointMatrix(3, 4f, 7f, 15f);
    private final FloatingPointMatrix m2 = new FloatingPointMatrix(2, 3f, 2f, 1f, 9f, 8f, 7f);
    private final FloatingPointMatrix i1 = FloatingPointMatrix.identity(5);

    @Test
    public void testConstruction(){
        System.out.println(m1);
        System.out.println(v1);
        System.out.println(m2);
        System.out.println(i1);
        System.out.println(m1.multiply(m2));
    }

    @Test
    public void testOperations(){
        assertEquals("negate", new FloatingPointMatrix(2, -1f, -2f), new FloatingPointMatrix(2, 1f, 2f).negate());

    }

    @Test
    public void testTransformation(){
        assertEquals("translation", translation4f(2, 3, 4).multiply(vec4f(5, 6, 7, 1)), vec4f(7, 9, 11, 1));
        assertEquals("scaling", scaling4f(2, 3, 4).multiply(vec4f(5, 6, 7, 1)), vec4f(10, 18, 28, 1));
        assertEquals("extract 1", new FloatingPointMatrix(2, 1, 3, 7, 9), new FloatingPointMatrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9).extract(1, 1));
        assertEquals("extract 2", new FloatingPointMatrix(2, 1, 3, 4, 6, 7, 9), new FloatingPointMatrix(3, 1, 2, 3, 4, 5, 6, 7, 8, 9).extract(-1, 1));
        assertEquals("determinant", 20L, new IntegerMatrix(4, 3, 1, 4, 5, 0, 2, 0, 0, 2, 0, 6, 2, -1, -2, -3, 0).determinant().longValue());
        assertEquals("cross", new IntegerMatrix(3, -15, -2, 39), new IntegerMatrix(3, 3, -3, 1).cross(new IntegerMatrix(3, 4, 9, 2)));
        assertEquals("cross", new FloatingPointMatrix(3, -15, -2, 39), new FloatingPointMatrix(3, 3, -3, 1).cross(new FloatingPointMatrix(3, 4, 9, 2)));
    }

    @Test
    public void testNormalize(){
        System.out.println(vec3f(1, 2, 3).normalize());
    }

    @Test
    public void testTranspose(){
        System.out.println(m1);
        System.out.println(m1.transpose());
    }
}
