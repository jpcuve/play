package com.messio.gl;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Created by jpc on 4/25/14.
 */
public class FloatingPointMatrix extends Matrix<Double> {
    private double[] data;

    protected FloatingPointMatrix(int rowDim, int colDim) {
        super(rowDim, colDim);
        this.data = new double[rowDim * colDim];
    }

    public FloatingPointMatrix(int rowDim, double... data){
        super(rowDim, data.length / rowDim);
        if (data.length != colDim * rowDim) throw new IllegalArgumentException();
        this.data = data;
    }

    public FloatBuffer getFloatBuffer(){
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        for (double d: data) buffer.put((float) d);
        buffer.flip();
        return buffer;
    }

    @Override
    public Double getValue(int aCol, int aRow) {
        return data[aCol* rowDim + aRow];
    }

    @Override
    public FloatingPointMatrix add(Matrix<? extends Number> m) {
        if (!(colDim == m.colDim && rowDim == m.rowDim)) throw new IllegalArgumentException();
        final FloatingPointMatrix mat = new FloatingPointMatrix(rowDim, colDim);
        for (int i = 0; i < mat.data.length; i++) mat.data[i] = data[i] + m.getValue(i / rowDim, i % rowDim).doubleValue();
        return mat;
    }

    @Override
    public FloatingPointMatrix subtract(Matrix<? extends Number> m) {
        if (!(colDim == m.colDim && rowDim == m.rowDim)) throw new IllegalArgumentException();
        final FloatingPointMatrix mat = new FloatingPointMatrix(rowDim, colDim);
        for (int i = 0; i < mat.data.length; i++) mat.data[i] = data[i] - m.getValue(i / rowDim, i % rowDim).doubleValue();
        return mat;
    }

    @Override
    public FloatingPointMatrix multiply(Matrix<? extends Number> m) {
        if (colDim != m.rowDim) throw new IllegalArgumentException();
        final FloatingPointMatrix mat = new FloatingPointMatrix(rowDim, m.colDim);
        for (int col = 0; col < mat.colDim; col++) for (int row = 0; row < mat.rowDim; row++){
            int index = col * mat.rowDim + row;
            for(int i = 0; i < colDim; i++){
                mat.data[index] += data[i * rowDim + row] * m.getValue(col, i).doubleValue();
            }
        }
        return mat;
    }

    @Override
    public FloatingPointMatrix extract(int removeCol, int removeRow) {
        int newRowDim = removeRow >= 0 && removeRow < rowDim ? rowDim - 1 : rowDim;
        int newColDim = removeCol >= 0 && removeCol < colDim ? colDim - 1 : colDim;
        final FloatingPointMatrix m = new FloatingPointMatrix(newRowDim, newColDim);
        int index = 0;
        for (int col = 0; col < colDim; col++) for (int row = 0; row < rowDim; row++) if (col != removeCol && row != removeRow) {
            m.data[index++] = data[col * rowDim + row];
        }
        return m;
    }

    @Override
    public FloatingPointMatrix transpose() {
        final FloatingPointMatrix m = new FloatingPointMatrix(colDim, rowDim);
        for (int i = 0; i < data.length; i++) m.data[(i % rowDim) * colDim+ (i / rowDim)] = data[i];
        return m;

    }

    @Override
    public Double determinant() {
        if (rowDim != colDim) throw new IllegalArgumentException();
        switch(rowDim){
            case 0:
                return 0.0;
            case 1:
                return data[0];
            case 2:
                return data[0] * data[3] - data[1] * data[2];
            case 3:
                return data[0] * (data[4] * data[8] - data[5] * data[7]) - data[3] * (data[1] * data[8] - data[2] * data[7]) + data[6] * (data[1] * data[5] - data[2] * data[4]);
            default:
                int sign = 1;
                double sum = 0;
                for (int i = 0; i < colDim; i++){
                    double pivot = data[i * rowDim];
                    if (pivot != 0) sum += sign * pivot * this.extract(i, 0).determinant();
                    sign = -sign;
                }
                return sum;
        }
    }

    @Override
    public FloatingPointMatrix negate() {
        final FloatingPointMatrix m = new FloatingPointMatrix(rowDim, colDim);
        for (int i = 0; i < data.length; i++) m.data[i] = -data[i];
        return m;
    }

    @Override
    public Double dot(Matrix<? extends Number> m) {
        if (!(colDim == m.colDim && rowDim == m.rowDim)) throw new IllegalArgumentException();
        double sum = 0;
        for (int i = 0; i < data.length; i++) sum += data[i] * m.getValue(i / rowDim, i % rowDim).doubleValue();
        return sum;
    }

    @Override
    public FloatingPointMatrix cross(Matrix<? extends Number> m) {
        if (!(isVec3() && m.isVec3())) throw new IllegalArgumentException();
        return new FloatingPointMatrix(3, 0, data[2], -data[1], -data[2], 0, data[0], data[1], -data[0], 0).multiply(m);
    }

    @Override
    public FloatingPointMatrix scalarDivide(Double divisor) {
        if (divisor == 0) throw new IllegalArgumentException();
        final FloatingPointMatrix m = new FloatingPointMatrix(rowDim, colDim);
        for (int i = 0; i < data.length; i++) m.data[i] = data[i] / divisor;
        return m;
    }

    @Override
    public Double getX() {
        if (colDim != 1 && rowDim < 1) throw new IllegalArgumentException();
        return data[0];
    }

    @Override
    public Double getY() {
        if (colDim != 1 && rowDim < 2) throw new IllegalArgumentException();
        return data[1];
    }

    @Override
    public Double getZ() {
        if (colDim != 1 && rowDim < 3) throw new IllegalArgumentException();
        return data[2];
    }

    @Override
    public Double getA() {
        if (colDim != 1 && rowDim < 4) throw new IllegalArgumentException();
        return data[3];
    }

    public static FloatingPointMatrix identity(int dim){
        final FloatingPointMatrix m = new FloatingPointMatrix(dim, dim);
        for (int i = 0; i < dim; i++) m.data[dim * i + i] = 1;
        return m;
    }

    public static FloatingPointMatrix vec2f(double c0r0, double c0r1){
        return new FloatingPointMatrix(2, c0r0, c0r1);
    }

    public static FloatingPointMatrix vec3f(double c0r0, double c0r1, double c0r2){
        return new FloatingPointMatrix(3, c0r0, c0r1, c0r2);
    }

    public static FloatingPointMatrix vec4f(double c0r0, double c0r1, double c0r2, double c0r3){
        return new FloatingPointMatrix(4, c0r0, c0r1, c0r2, c0r3);
    }

    public static FloatingPointMatrix mat2f(double c0r0, double c0r1, double c1r0, double c1r1){
        return new FloatingPointMatrix(2, c0r0, c0r1, c1r0, c1r1);
    }

    public static FloatingPointMatrix mat3f(double c0r0, double c0r1, double c0r2, double c1r0, double c1r1, double c1r2, double c2r0, double c2r1, double c2r2){
        return new FloatingPointMatrix(3, c0r0, c0r1, c0r2, c1r0, c1r1, c1r2, c2r0, c2r1, c2r2);
    }

    public static FloatingPointMatrix mat4f(double c0r0, double c0r1, double c0r2, double c0r3, double c1r0, double c1r1, double c1r2, double c1r3, double c2r0, double c2r1, double c2r2, double c2r3, double c3r0, double c3r1, double c3r2, double c3r3){
        return new FloatingPointMatrix(3, c0r0, c0r1, c0r2, c0r3, c1r0, c1r1, c1r2, c1r3, c2r0, c2r1, c2r2, c2r3, c3r0, c3r1, c3r2, c3r3);
    }

    public static FloatingPointMatrix translation4f(double tx, double ty, double tz){
        return new FloatingPointMatrix(4, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, tx, ty, tz, 1);
    }

    public static FloatingPointMatrix scaling4f(double sx, double sy, double sz){
        return new FloatingPointMatrix(4, sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0, 0, 0, 0, 1);
    }

    public static FloatingPointMatrix rx4f(int t){
        return new FloatingPointMatrix(4, 1, 0, 0, 0, 0, Angle.cos(t), Angle.sin(t), 0, 0, -Angle.sin(t), Angle.cos(t), 0, 0, 0, 0, 1);
    }

    public static FloatingPointMatrix ry4f(int t){
        return new FloatingPointMatrix(4, Angle.cos(t), 0, -Angle.sin(t), 0, 0, 1, 0, 0, Angle.sin(t), 0, Angle.cos(t), 0, 0, 0, 0, 1);
    }

    public static FloatingPointMatrix rz4f(int t){
        return new FloatingPointMatrix(4, Angle.cos(t), Angle.sin(t), 0, 0, -Angle.sin(t), Angle.cos(t), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    public static FloatingPointMatrix lookAt(FloatingPointMatrix eye, FloatingPointMatrix center, FloatingPointMatrix up){
        if (!eye.isVec3() || !center.isVec3() || !up.isVec3()) throw new IllegalArgumentException();
        final FloatingPointMatrix z = eye.subtract(center).normalize();
        final FloatingPointMatrix x = up.cross(z).normalize();
        final FloatingPointMatrix y = z.cross(x);
        final FloatingPointMatrix orientation = new FloatingPointMatrix(4, x.data[0], y.data[0], z.data[0], 0, x.data[1], y.data[1], z.data[1], 0, x.data[2], y.data[2], z.data[2], 0, 0, 0, 0, 1);
        final FloatingPointMatrix translation = new FloatingPointMatrix(4, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -eye.data[0], -eye.data[1], -eye.data[2], 1);
        return orientation.multiply(translation);
    }

    public static FloatingPointMatrix firstPerson(FloatingPointMatrix eye, int pitch, int yaw){
        if (!eye.isVec3()) throw new IllegalArgumentException();
        if (pitch < -Angle.UNITS_PER_HALF_PI || pitch > Angle.UNITS_PER_HALF_PI) throw new IllegalArgumentException();
        final FloatingPointMatrix x = vec3f(Angle.cos(yaw), 0, -Angle.sin(yaw));
        final FloatingPointMatrix y = vec3f(Angle.sin(yaw) * Angle.sin(pitch), Angle.cos(pitch), Angle.cos(yaw) * Angle.sin(pitch));
        final FloatingPointMatrix z = vec3f(Angle.sin(yaw) * Angle.cos(pitch), -Angle.sin(pitch), Angle.cos(yaw) * Angle.cos(pitch));
        return new FloatingPointMatrix(4, x.data[0], y.data[0], z.data[0], 0, x.data[1], y.data[1], z.data[1], 0, x.data[2], y.data[2], z.data[2], 0, -x.dot(eye), - y.dot(eye), -z.dot(eye), 1);
    }

    public static FloatingPointMatrix perspectiveProjection(int width, int height, int fov, double near, double far){
        if (far <= near) throw new IllegalArgumentException();
        double sx = 2.0 * near * fov / width;
        double sy = 2.0 * near * fov / height;
        double sz = - (far + near) / (far - near);
        double pz = - 2.0 * far * near / (far - near);
        return new FloatingPointMatrix(4, sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, -1, 0, 0, pz, 0);
    }

    public static FloatingPointMatrix orthographicProjection(int width, int height, int fov, double near, double far){
        if (far <= near) throw new IllegalArgumentException();
        double sx = 2.0 * fov / width;
        double sy = 2.0 * fov / height;
        double sz = - 2.0 / (far - near);
        double pz = - (far + near) / (far - near);
        return new FloatingPointMatrix(4, sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, -1, 0, 0, pz, 0);
    }


}
