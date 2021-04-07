// 
// Decompiled by Procyon v0.5.36
// 

package com.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Panel3D extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private Mesh mesh;
    private Vector3 lightDirection;
    private Vector3 viewerPosition;
    private Vector3 cameraPosition;
    private Vector3 cameraRotation;
    private int lastX;
    private int lastY;

    public Panel3D() {
        this.mesh = new Mesh(new Face[0]);
        this.lightDirection = new Vector3(0.0, 0.25, -1.0).unit();
        this.viewerPosition = new Vector3(0.0, 0.0, -325.0);
        this.cameraPosition = new Vector3(0.0, 0.0, -2.8);
        this.cameraRotation = new Vector3(0.0, 0.0, 0.0);
        this.lastX = 0;
        this.lastY = 0;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    public void setMesh(final Mesh mesh) {
        this.mesh = mesh;
        this.repaint();
    }

    public void setLightDirection(final Vector3 lightDirection) {
        this.lightDirection = lightDirection;
        this.repaint();
    }

    public void setViewerPosition(final Vector3 viewerPosition) {
        this.viewerPosition = viewerPosition;
        this.repaint();
    }

    public void setCameraPosition(final Vector3 cameraPosition) {
        this.cameraPosition = cameraPosition;
        this.repaint();
    }

    public void setCameraRotation(final Vector3 cameraRotation) {
        this.cameraRotation = cameraRotation;
        this.repaint();
    }

    private Vector3 toCameraCoordinates(final Vector3 v) {
        return Matrix44.rotationX(-this.cameraRotation.x).mul(Matrix44.rotationY(-this.cameraRotation.y).mul(Matrix44.rotationZ(-this.cameraRotation.z).mul(v.sub(this.cameraPosition))));
    }

    private Vector3 perspectiveProjection(final Vector3 v) {
        return new Vector3(this.getWidth() / 2.0 + (-v.x - this.viewerPosition.x) * (this.viewerPosition.z / v.z), this.getHeight() / 2.0 + (v.y - this.viewerPosition.y) * (this.viewerPosition.z / v.z), 0.0);
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final Face[] faces = Arrays.copyOf(this.mesh.faces, this.mesh.faces.length);
        Arrays.sort(faces, (f1, f2) -> Double.compare(f2.centroid().z, f1.centroid().z));

        final Face[] pFaces = new Face[faces.length];
        for (int i = 0; i < pFaces.length; ++i) {
            final Vector3[] vertices = new Vector3[faces[i].vertices.length];
            for (int j = 0; j < vertices.length; ++j) {
                vertices[j] = this.perspectiveProjection(this.toCameraCoordinates(faces[i].vertices[j]));
            }
            pFaces[i] = faces[i].setVertices(vertices);
        }

        final Color backfacingColor = new Color((4 * this.getBackground().getRed() + 32) / 5, (4 * this.getBackground().getGreen() + 32) / 5, (4 * this.getBackground().getBlue() + 32) / 5);
        Face[] array;
        for (int length = (array = pFaces).length, k = 0; k < length; ++k) {
            final Face pFace = array[k];
            final Polygon polygon = new Polygon();
            Vector3[] vertices2;
            for (int length2 = (vertices2 = pFace.vertices).length, l = 0; l < length2; ++l) {
                final Vector3 v = vertices2[l];
                polygon.addPoint((int) v.x, (int) v.y);
            }
            final Plane plane = new Plane(pFace.vertices[0], pFace.vertices[1], pFace.vertices[2]);
            if (plane.n.z >= 0.0) {
                final double light = Math.abs(this.lightDirection.dot(plane.n));
                final float[] hsbColor = Color.RGBtoHSB(pFace.color.getRed(), pFace.color.getGreen(), pFace.color.getBlue(), null);
                final Color fillColor = new Color(Color.HSBtoRGB(hsbColor[0], (float) (0.875 + 0.125 * light) * hsbColor[1], (float) (0.875 + 0.125 * light) * hsbColor[2]));
                g2.setColor(fillColor);
                g2.fillPolygon(polygon);
                final Color outlineColor = new Color(Color.HSBtoRGB(hsbColor[0], (float) (0.9 * (0.875 + 0.125 * light) * hsbColor[1]), (float) (0.9 * (0.875 + 0.125 * light) * hsbColor[2])));
                g2.setColor(outlineColor);
                g2.drawPolygon(polygon);
            } else {
                g2.setColor(backfacingColor);
                g2.fillPolygon(polygon);
            }
        }
    }

    @Override
    public void mouseClicked(final MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(final MouseEvent arg0) {
    }

    @Override
    public void mouseExited(final MouseEvent arg0) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        this.lastX = e.getX();
        this.lastY = e.getY();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        final double angleX = (e.getY() - this.lastY) / 50.0;
        final double angleY = (e.getX() - this.lastX) / 50.0;
        this.mesh = this.mesh.transform(Matrix44.rotationZ(this.cameraRotation.z).mul(Matrix44.rotationY(this.cameraRotation.y).mul(Matrix44.rotationX(this.cameraRotation.x).mul(Matrix44.rotationX(angleX).mul(Matrix44.rotationY(angleY).mul(Matrix44.rotationX(-this.cameraRotation.x).mul(Matrix44.rotationY(-this.cameraRotation.y).mul(Matrix44.rotationZ(-this.cameraRotation.z)))))))));
        this.lastX = e.getX();
        this.lastY = e.getY();
        this.repaint();
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        final Vector3 direction = this.cameraPosition.unit();
        final Vector3 newPosition = this.cameraPosition.add(direction.mul(0.1 * e.getWheelRotation()));
        if (1.0 < newPosition.norm() && newPosition.norm() < 50.0) {
            this.cameraPosition = newPosition;
        }
        this.repaint();
    }
}
