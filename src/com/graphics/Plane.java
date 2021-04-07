// 
// Decompiled by Procyon v0.5.36
// 

package com.graphics;

public class Plane {

    public Vector3 p;
    public Vector3 n;

    public Plane(final Vector3 p, final Vector3 n) {
        this.p = p;
        this.n = n;
    }

    public Plane(final Vector3 v1, final Vector3 v2, final Vector3 v3) {
        this.p = v1.add(v2).add(v3).mul(0.333_333_333_333_333_3);
        this.n = v2.sub(v1).cross(v3.sub(v1)).unit();
    }

    @Override
    public String toString() {
        return "Plane{" +
                "p=" + p +
                ", n=" + n +
                '}';
    }
}
