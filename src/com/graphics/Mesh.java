// 
// Decompiled by Procyon v0.5.36
// 

package com.graphics;

import java.awt.*;
import java.util.ArrayList;

public class Mesh {

    public final Face[] faces;

    public Mesh(final Face[] faces) {
        this.faces = faces;
    }

    public Mesh transform(final Matrix44 matrix) {
        final Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; ++i) {
            faces[i] = this.faces[i].transform(matrix);
        }
        return new Mesh(faces);
    }

    public Mesh rotateHalfSpace(final Plane plane, final double angle) {
        final Matrix44 matrix = Matrix44.rotation(plane.n, angle);
        final Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; ++i) {
            faces[i] = this.faces[i];
            if (faces[i].centroid().sub(plane.p).dot(plane.n) >= 0.0) {
                faces[i] = faces[i].transform(matrix);
            }
        }
        return new Mesh(faces);
    }

    public Mesh shortenFaces(final double length) {
        final Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; ++i) {
            faces[i] = this.faces[i].shorten(length);
        }
        return new Mesh(faces);
    }

    public Mesh softenFaces(final double length) {
        final Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; ++i) {
            faces[i] = this.faces[i].soften(length);
        }
        return new Mesh(faces);
    }

    public Mesh clip(final Plane plane) {
        final ArrayList<Face> faces = new ArrayList<>();
        for (int i = 0; i < this.faces.length; ++i) {
            final Face face = this.faces[i].clip(plane);
            if (face.vertices.length > 0) {
                faces.add(face);
            }
        }
        final Face[] facesArray = new Face[faces.size()];
        faces.toArray(facesArray);
        return new Mesh(facesArray);
    }

    public Mesh cut(final Plane plane, final double width) {
        final Mesh front = this.clip(new Plane(plane.p.add(plane.n.mul(width / 2.0)), plane.n));
        final Mesh back = this.clip(new Plane(plane.p.sub(plane.n.mul(width / 2.0)), plane.n.neg()));
        return front.union(back);
    }

    public Mesh union(final Mesh mesh) {
        final Face[] faces = new Face[this.faces.length + mesh.faces.length];
        int next = 0;
        for (int i = 0; i < this.faces.length; ++i) {
            faces[next] = this.faces[i];
            ++next;
        }
        for (int i = 0; i < mesh.faces.length; ++i) {
            faces[next] = mesh.faces[i];
            ++next;
        }
        return new Mesh(faces);
    }

    public static Mesh cube(final Color[] colors) {
        final double a = 0.5;
        final Vector3[] vertices = {
                new Vector3(-a, -a, -a),
                new Vector3(-a, -a, a),
                new Vector3(-a, a, -a),
                new Vector3(-a, a, a),
                new Vector3(a, -a, -a),
                new Vector3(a, -a, a),
                new Vector3(a, a, -a),
                new Vector3(a, a, a)};
        final Face[] faces = {
                new Face(new Vector3[]{
                        vertices[0], vertices[1], vertices[3], vertices[2]}, colors[0]),
                new Face(new Vector3[]{
                        vertices[1], vertices[5], vertices[7], vertices[3]}, colors[1]),
                new Face(new Vector3[]{
                        vertices[0], vertices[4], vertices[5], vertices[1]}, colors[2]),
                new Face(new Vector3[]{
                        vertices[4], vertices[6], vertices[7], vertices[5]}, colors[3]),
                new Face(new Vector3[]{
                        vertices[0], vertices[2], vertices[6], vertices[4]}, colors[4]),
                new Face(new Vector3[]{
                        vertices[2], vertices[3], vertices[7], vertices[6]}, colors[5])};
        return new Mesh(faces);
    }

    public static Mesh tetrahedron(final Color[] colors) {
        final double a = 1.5;
        final double h = Math.sqrt(3.0) / 2.0 * a;
        final double h2 = 2.0 * Math.sqrt(2.0) / 3.0 * h;
        final Vector3[] vertices = {
                new Vector3(0.0, -h2 / 4.0, 2.0 * h / 3.0),
                new Vector3(-a / 2.0, -h2 / 4.0, -h / 3.0),
                new Vector3(a / 2.0, -h2 / 4.0, -h / 3.0),
                new Vector3(0.0, 3.0 * h2 / 4.0, 0.0)};
        final Face[] faces = {
                new Face(new Vector3[]{vertices[0], vertices[1], vertices[2]}, colors[0]),
                new Face(new Vector3[]{vertices[0], vertices[3], vertices[1]}, colors[1]),
                new Face(new Vector3[]{vertices[0], vertices[2], vertices[3]}, colors[2]),
                new Face(new Vector3[]{vertices[1], vertices[3], vertices[2]}, colors[3])};
        return new Mesh(faces);
    }

    public static Mesh dodecahedron(final Color[] colors) {
        final double a = 0.85 / Math.sqrt(3.0);
        final double b = 0.85 * Math.sqrt((3.0 - Math.sqrt(5.0)) / 6.0);
        final double c = 0.85 * Math.sqrt((3.0 + Math.sqrt(5.0)) / 6.0);
        final Face[] faces = {
                new Face(new Vector3[]{
                        new Vector3(a, a, a),
                        new Vector3(b, c, 0.0),
                        new Vector3(-b, c, 0.0),
                        new Vector3(-a, a, a),
                        new Vector3(0.0, b, c)}, colors[0]),
                new Face(new Vector3[]{
                        new Vector3(a, a, a),
                        new Vector3(0.0, b, c),
                        new Vector3(0.0, -b, c),
                        new Vector3(a, -a, a),
                        new Vector3(c, 0.0, b)}, colors[1]),
                new Face(new Vector3[]{
                        new Vector3(c, 0.0, b),
                        new Vector3(a, -a, a),
                        new Vector3(b, -c, 0.0),
                        new Vector3(a, -a, -a),
                        new Vector3(c, 0.0, -b)}, colors[2]),
                new Face(new Vector3[]{
                        new Vector3(-b, c, 0.0),
                        new Vector3(-a, a, -a),
                        new Vector3(-c, 0.0, -b),
                        new Vector3(-c, 0.0, b),
                        new Vector3(-a, a, a)}, colors[3]),
                new Face(new Vector3[]{
                        new Vector3(a, -a, -a),
                        new Vector3(0.0, -b, -c),
                        new Vector3(0.0, b, -c),
                        new Vector3(a, a, -a),
                        new Vector3(c, 0.0, -b)}, colors[4]),
                new Face(new Vector3[]{
                        new Vector3(-a, -a, -a),
                        new Vector3(-b, -c, 0.0),
                        new Vector3(-a, -a, a),
                        new Vector3(-c, 0.0, b),
                        new Vector3(-c, 0.0, -b)}, colors[5]),
                new Face(new Vector3[]{
                        new Vector3(a, a, a),
                        new Vector3(c, 0.0, b),
                        new Vector3(c, 0.0, -b),
                        new Vector3(a, a, -a),
                        new Vector3(b, c, 0.0)}, colors[6]),
                new Face(new Vector3[]{
                        new Vector3(b, c, 0.0),
                        new Vector3(a, a, -a),
                        new Vector3(0.0, b, -c),
                        new Vector3(-a, a, -a),
                        new Vector3(-b, c, 0.0)}, colors[7]),
                new Face(new Vector3[]{
                        new Vector3(0.0, b, c),
                        new Vector3(-a, a, a),
                        new Vector3(-c, 0.0, b),
                        new Vector3(-a, -a, a),
                        new Vector3(0.0, -b, c)}, colors[8]),
                new Face(new Vector3[]{
                        new Vector3(-a, -a, a),
                        new Vector3(-b, -c, 0.0),
                        new Vector3(b, -c, 0.0),
                        new Vector3(a, -a, a),
                        new Vector3(0.0, -b, c)}, colors[9]),
                new Face(new Vector3[]{
                        new Vector3(-a, -a, -a),
                        new Vector3(-c, 0.0, -b),
                        new Vector3(-a, a, -a),
                        new Vector3(0.0, b, -c),
                        new Vector3(0.0, -b, -c)}, colors[10]),
                new Face(new Vector3[]{
                        new Vector3(-a, -a, -a),
                        new Vector3(0.0, -b, -c),
                        new Vector3(a, -a, -a),
                        new Vector3(b, -c, 0.0),
                        new Vector3(-b, -c, 0.0)}, colors[11])};
        return new Mesh(faces);
    }
}
