//
// Decompiled by Procyon v0.5.36
//

package com.graphics;

public class Matrix44 {

    public final double[][] values;

    public Matrix44(final double[][] matrix) {
        this.values = matrix;
    }

    public Matrix44 mul(final Matrix44 m) {
        final double[][] vals = new double[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                vals[i][j] = 0.0;
                for (int k = 0; k < 4; ++k) {
                    final double[] array = vals[i];
                    array[j] += this.values[i][k] * m.values[k][j];
                }
            }
        }
        return new Matrix44(vals);
    }

    public Vector3 mul(final Vector3 v) {
        return new Vector3(this.values[0][0] * v.x + this.values[0][1] * v.y + this.values[0][2] * v.z + this.values[0][3], this.values[1][0] * v.x + this.values[1][1] * v.y + this.values[1][2] * v.z + this.values[1][3], this.values[2][0] * v.x + this.values[2][1] * v.y + this.values[2][2] * v.z + this.values[2][3]);
    }

    public static Matrix44 translation(final Vector3 v) {
        return new Matrix44(new double[][]{{1.0, 0.0, 0.0, v.x}, {0.0, 1.0, 0.0, v.y}, {0.0, 0.0, 1.0, v.z}, {0.0, 0.0, 0.0, 1.0}});
    }

    public static Matrix44 rotationX(final double a) {
        return new Matrix44(new double[][]{{1.0, 0.0, 0.0, 0.0}, {0.0, Math.cos(a), Math.sin(a), 0.0}, {0.0, -Math.sin(a), Math.cos(a), 0.0}, {0.0, 0.0, 0.0, 1.0}});
    }

    public static Matrix44 rotationY(final double a) {
        return new Matrix44(new double[][]{{Math.cos(a), 0.0, -Math.sin(a), 0.0}, {0.0, 1.0, 0.0, 0.0}, {Math.sin(a), 0.0, Math.cos(a), 0.0}, {0.0, 0.0, 0.0, 1.0}});
    }

    public static Matrix44 rotationZ(final double a) {
        return new Matrix44(new double[][]{{Math.cos(a), Math.sin(a), 0.0, 0.0}, {-Math.sin(a), Math.cos(a), 0.0, 0.0}, {0.0, 0.0, 1.0, 0.0}, {0.0, 0.0, 0.0, 1.0}});
    }

    public static Matrix44 rotation(final Vector3 v, final double a) {
        final double c = Math.cos(a);
        final double s = Math.sin(a);
        final double x = v.x;
        final double y = v.y;
        final double z = v.z;
        return new Matrix44(new double[][]{{1.0 + (1.0 - c) * (x * x - 1.0), -z * s + (1.0 - c) * x * y, y * s + (1.0 - c) * x * z, 0.0}, {z * s + (1.0 - c) * x * y, 1.0 + (1.0 - c) * (y * y - 1.0), -x * s + (1.0 - c) * y * z, 0.0}, {-y * s + (1.0 - c) * x * z, x * s + (1.0 - c) * y * z, 1.0 + (1.0 - c) * (z * z - 1.0), 0.0}, {0.0, 0.0, 0.0, 1.0}});
    }
}
