package com.messio.gl;

import com.messio.sample.VirtualCamera;
import org.junit.Test;

import static com.messio.gl.FloatingPointMatrix.*;

/**
 * Created by jpc on 01/05/14.
 */
public class VirtualCameraTest {

    @Test
    public void testVirtualCamera(){
        final FloatingPointMatrix point = vec4f(5, 0, 0, 1);
        final FloatingPointMatrix model = rz4f(Angle.UNITS_PER_HALF_PI / 3);
        final FloatingPointMatrix lookAt = VirtualCamera.getLookAtViewMatrix(vec3f(0, 0, 10), vec3f(0, 0, 0), vec3f(0, 1, 0));
        System.out.println("lookAt: " + lookAt);
        final FloatingPointMatrix perspective = VirtualCamera.getPerspectiveProjectionMatrix(300, 200, 1000, 3.0, 20.0);
        System.out.println("perspective: " + perspective);

        System.out.println("model -> " + model.multiply(point));
        System.out.println("model, lookAt -> " + lookAt.multiply(model.multiply(point)));
        System.out.println("model, lookAt, perspective -> " + perspective.multiply(lookAt.multiply(model.multiply(point))));
    }
}
