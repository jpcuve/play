package com.messio.gl;

import static com.messio.gl.FloatingPointMatrix.vec3f;

/**
 * Created by jpc on 4/25/14.
 */
public abstract class Matrix<N extends Number> {

    protected int rowDim;
    protected int colDim;

    public abstract N getValue(int aCol, int aRow);
    public abstract Matrix<N> add(Matrix<? extends Number> m);
    public abstract Matrix<N> subtract(Matrix<? extends Number> m);
    public abstract Matrix<N> multiply(Matrix<? extends Number> m);
    public abstract Matrix<N> negate();
    public abstract Matrix<N> scalarDivide(N divisor);
    public abstract Matrix<N> extract(int removeCol, int removeRow);
    public abstract Matrix<N> transpose();
    public abstract N determinant();
    public abstract N dot(Matrix<? extends Number> m);
    public abstract Matrix<N> cross(Matrix<? extends Number> m);
    public abstract N getX();
    public abstract N getY();
    public abstract N getZ();
    public abstract N getA();

    protected Matrix(int rowDim, int colDim) {
        this.rowDim = rowDim;
        this.colDim = colDim;
    }

    public int getRowDim() {
        return rowDim;
    }

    public int getColDim() {
        return colDim;
    }

    public boolean isVector(){
        return colDim == 1;
    }

    public boolean isVec3(){
        return colDim == 1 && rowDim == 3;
    }

    public boolean isVec4(){
        return colDim == 1 && rowDim == 4;
    }

    public boolean isSquare(){
        return colDim == rowDim;
    }

    public boolean isMat3(){
        return colDim == 3 && rowDim == 3;
    }

    public boolean isMat4(){
        return colDim == 4 && rowDim == 4;
    }

    public double length(){
        return Math.sqrt(this.dot(this).doubleValue());
    }

    public FloatingPointMatrix normalize(){
        final double l = length();
        final double[] data = new double[rowDim * colDim];
        for (int row = 0; row < rowDim; row++) for (int col = 0; col < colDim; col++) data[col* rowDim + row] = getValue(col, row).doubleValue() / l;
        return new FloatingPointMatrix(rowDim, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix)) return false;
        final Matrix m = (Matrix) obj;
        if (!(colDim == m.colDim && rowDim == m.rowDim)) return false;
        for (int row = 0; row < rowDim; row++) for (int col = 0; col < colDim; col++) if (!getValue(col, row).equals(m.getValue(col, row))) return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        boolean sp1 = false;
        sb.append(rowDim).append('x').append(colDim).append('[');
        for (int row = 0; row < rowDim; row++) {
            if (sp1) sb.append(" | ");
            boolean sp2 = false;
            for (int col = 0; col < colDim; col++) {
                if (sp2) sb.append(' ');
                sb.append(getValue(col, row));
                sp2 = true;
            }
            sp1 = true;
        }
        sb.append(']');
        return sb.toString();
    }


}
