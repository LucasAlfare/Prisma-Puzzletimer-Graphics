// 
// Decompiled by Procyon v0.5.36
// 

package com.graphics;

import java.awt.*;
import java.util.ArrayList;

public class Face {
    public final Vector3[] vertices;
    public final Color color;

    public Face(final Vector3[] vertices, final Color color) {
        this.vertices = vertices;
        this.color = color;
    }

    public Face setVertices(final Vector3[] vertices) {
        return new Face(vertices, this.color);
    }

    public Face setColor(final Color color) {
        return new Face(this.vertices, color);
    }

    public Vector3 centroid() {
        Vector3 sum = new Vector3(0.0, 0.0, 0.0);
        Vector3[] vertices;
        for (int length = (vertices = this.vertices).length, i = 0; i < length; ++i) {
            final Vector3 v = vertices[i];
            sum = sum.add(v);
        }
        return sum.mul(1.0 / this.vertices.length);
    }

    public Face transform(final Matrix44 matrix) {
        final Vector3[] vertices = new Vector3[this.vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            vertices[i] = matrix.mul(this.vertices[i]);
        }
        return this.setVertices(vertices);
    }

    public Face shorten(final double length) {
        final Vector3 centroid = this.centroid();
        final Vector3[] vertices = new Vector3[this.vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            final Vector3 d = this.vertices[i].sub(centroid).unit();
            vertices[i] = this.vertices[i].sub(d.mul(length));
        }
        return this.setVertices(vertices);
    }

    public Face soften(final double length) {
        final ArrayList<Vector3> vertices = new ArrayList<>();
        for (int i = 0; i < this.vertices.length; ++i) {
            final Vector3 v1 = this.vertices[i];
            final Vector3 v2 = this.vertices[(i + 1) % this.vertices.length];
            if (v2.sub(v1).norm() > 2.0 * length) {
                vertices.add(v1.add(v2.sub(v1).unit().mul(length)));
                vertices.add(v2.add(v1.sub(v2).unit().mul(length)));
            } else {
                vertices.add(v1.add(v2).mul(0.5));
            }
        }
        final Vector3[] verticesArray = new Vector3[vertices.size()];
        vertices.toArray(verticesArray);
        return this.setVertices(verticesArray);
    }

    public Face clip(final Plane plane) {
        final double EPSILON = 0.01;
        final int INSIDE = 0;
        final int FRONT = 1;
        final int BACK = 2;
        final int[] position = new int[this.vertices.length];
        boolean allFront = true;
        boolean allBack = true;
        for (int i = 0; i < this.vertices.length; ++i) {
            final double d = this.vertices[i].sub(plane.p).dot(plane.n);
            if (d > EPSILON) {
                position[i] = FRONT;
                allBack = false;
            } else if (d < -EPSILON) {
                position[i] = BACK;
                allFront = false;
            } else {
                position[i] = INSIDE;
            }
        }
        if (allBack) {
            return this.setVertices(new Vector3[0]);
        }
        if (allFront) {
            return this;
        }
        final ArrayList<Vector3> vertices = new ArrayList<>();
        for (int j = 0; j < this.vertices.length; ++j) {
            final Vector3 v1 = this.vertices[j];
            final Vector3 v2 = this.vertices[(j + 1) % this.vertices.length];
            final int p1 = position[j];
            final int p2 = position[(j + 1) % this.vertices.length];
            if (p1 != BACK) {
                vertices.add(v1);
            }
            if ((p1 == FRONT && p2 == BACK) || (p1 == BACK && p2 == FRONT)) {
                final double t = -(plane.n.dot(v1) + plane.n.neg().dot(plane.p)) / v2.sub(v1).dot(plane.n);
                vertices.add(v1.add(v2.sub(v1).mul(t)));
            }
        }
        final Vector3[] verticesArray = new Vector3[vertices.size()];
        vertices.toArray(verticesArray);
        return this.setVertices(verticesArray);
    }
}
