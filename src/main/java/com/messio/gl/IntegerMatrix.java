package com.messio.gl;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by jpc on 26/04/14.
 */
public class IntegerMatrix extends Matrix<Integer> {
    private int[] data;

    protected IntegerMatrix(int rowDim, int colDim) {
        super(rowDim, colDim);
        this.data = new int[rowDim * colDim];
    }

    public IntegerMatrix(int rowDim, int... data){
        super(rowDim, data.length / rowDim);
        if (data.length != colDim * rowDim) throw new IllegalArgumentException();
        this.data = data;
    }

    public ByteBuffer getByteBuffer(){
        final ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        for (int i: data) buffer.put((byte) i);
        buffer.flip();
        return buffer;
    }

    @Override
    public Integer getValue(int aCol, int aRow) {
        return data[aCol* rowDim + aRow];
    }

    @Override
    public IntegerMatrix add(Matrix<? extends Number> m) {
        if (!(colDim == m.colDim && rowDim == m.rowDim)) throw new IllegalArgumentException();
        final IntegerMatrix mat = new IntegerMatrix(rowDim, colDim);
        for (int i = 0; i < mat.data.length; i++) mat.data[i] = (byte) (data[i] + m.getValue(i / rowDim, i % rowDim).intValue());
        return mat;
    }

    @Override
    public IntegerMatrix subtract(Matrix<? extends Number> m) {
        if (!(colDim == m.colDim && rowDim == m.rowDim)) throw new IllegalArgumentException();
        final IntegerMatrix mat = new IntegerMatrix(rowDim, colDim);
        for (int i = 0; i < mat.data.length; i++) mat.data[i] = (byte) (data[i] + m.getValue(i / rowDim, i % rowDim).intValue());
        return mat;
    }

    @Override
    public IntegerMatrix multiply(Matrix<? extends Number> m) {
        if (colDim != m.rowDim) throw new IllegalArgumentException();
        final IntegerMatrix mat = new IntegerMatrix(rowDim, m.colDim);
        for (int col = 0; col < mat.colDim; col++) for (int row = 0; row < mat.rowDim; row++){
            int index = col * mat.rowDim + row;
            for(int i = 0; i < colDim; i++){
                mat.data[index] += data[i * rowDim + row] * m.getValue(col, i).intValue();
            }
        }
        return mat;
    }

    @Override
    public IntegerMatrix extract(int removeCol, int removeRow) {
        int newRowDim = removeRow >= 0 && removeRow < rowDim ? rowDim - 1 : rowDim;
        int newColDim = removeCol >= 0 && removeCol < colDim ? colDim - 1 : colDim;
        final IntegerMatrix m = new IntegerMatrix(newRowDim, newColDim);
        int index = 0;
        for (int col = 0; col < colDim; col++) for (int row = 0; row < rowDim; row++) if (col != removeCol && row != removeRow) {
            m.data[index++] = data[col * rowDim + row];
        }
        return m;
    }

    @Override
    public IntegerMatrix transpose() {
        final IntegerMatrix m = new IntegerMatrix(colDim, rowDim);
        for (int i = 0; i < data.length; i++) m.data[(i % rowDim) * colDim+ (i / rowDim)] = data[i];
        return m;
    }

    @Override
    public Integer determinant() {
        if (rowDim != colDim) throw new IllegalArgumentException();
        switch(rowDim){
            case 0:
                return 0;
            case 1:
                return data[0];
            case 2:
                return data[0] * data[3] - data[1] * data[2];
            case 3:
                return data[0] * (data[4] * data[8] - data[5] * data[7]) - data[3] * (data[1] * data[8] - data[2] * data[7]) + data[6] * (data[1] * data[5] - data[2] * data[4]);
            default:
                int sign = 1;
                int sum = 0;
                for (int i = 0; i < colDim; i++){ // find line or row that has the most zeros to speed this up
                    int pivot = data[i * rowDim];
                    if (pivot != 0) sum += sign * pivot * this.extract(i, 0).determinant();
                    sign = -sign;
                }
                return sum;
        }
    }

    @Override
    public IntegerMatrix scalarDivide(Integer divisor) {
        if (divisor == 0) throw new IllegalArgumentException();
        final IntegerMatrix m = new IntegerMatrix(rowDim, colDim);
        for (int i = 0; i < data.length; i++) m.data[i] = data[i] / divisor;
        return m;
    }

    @Override
    public IntegerMatrix negate() {
        final IntegerMatrix m = new IntegerMatrix(rowDim, colDim);
        for (int i = 0; i < data.length; i++) m.data[i] = (byte) -data[i];
        return m;
    }

    @Override
    public Integer dot(Matrix<? extends Number> m) {
        if (!(colDim == m.colDim && rowDim == m.rowDim)) throw new IllegalArgumentException();
        int sum = 0;
        for (int i = 0; i < data.length; i++) sum += data[i] * m.getValue(i + colDim, i % colDim).intValue();
        return sum;
    }

    @Override
    public IntegerMatrix cross(Matrix<? extends Number> m) {
        if (!(isVec3() && m.isVec3())) throw new IllegalArgumentException();
        return new IntegerMatrix(3, 0, data[2], -data[1], -data[2], 0, data[0], data[1], -data[0], 0).multiply(m);
    }

    @Override
    public Integer getX() {
        if (colDim != 1 && rowDim < 1) throw new IllegalArgumentException();
        return data[0];
    }

    @Override
    public Integer getY() {
        if (colDim != 1 && rowDim < 2) throw new IllegalArgumentException();
        return data[1];
    }

    @Override
    public Integer getZ() {
        if (colDim != 1 && rowDim < 3) throw new IllegalArgumentException();
        return data[2];
    }

    @Override
    public Integer getA() {
        if (colDim != 1 && rowDim < 4) throw new IllegalArgumentException();
        return data[3];
    }

    public static IntegerMatrix vec2i(int c0r0, int c0r1){
        return new IntegerMatrix(2, c0r0, c0r1);
    }

    public static IntegerMatrix vec3i(int c0r0, int c0r1, int c0r2){
        return new IntegerMatrix(3, c0r0, c0r1, c0r2);
    }

    public static IntegerMatrix vec4i(int c0r0, int c0r1, int c0r2, int c0r3){
        return new IntegerMatrix(4, c0r0, c0r1, c0r2, c0r3);
    }




}
