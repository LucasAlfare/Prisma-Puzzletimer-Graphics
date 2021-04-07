// 
// Decompiled by Procyon v0.5.36
// 

package com.graphics;

public class Vector3 {

    public final double x;
    public final double y;
    public final double z;

    public Vector3(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 neg() {
        return new Vector3(-this.x, -this.y, -this.z);
    }

    public Vector3 add(final Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector3 sub(final Vector3 v) {
        return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vector3 mul(final double s) {
        return new Vector3(s * this.x, s * this.y, s * this.z);
    }

    public double dot(final Vector3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3 cross(final Vector3 v) {
        return new Vector3(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
    }

    public double norm() {
        return Math.sqrt(this.dot(this));
    }

    public Vector3 unit() {
        return this.mul(1.0 / this.norm());
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
