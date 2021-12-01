package dev.cerus.transparentmaps.nms;

import java.awt.Color;

public class MapColor {

    private static final double[] MODIFIERS = new double[] {0.71, 0.86, 1, 0.53};

    private final int id;
    private final int r;
    private final int g;
    private final int b;

    public MapColor(final int id, final int r, final int g, final int b) {
        this.id = id;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private Color getVariant(final int i) {
        return new Color(
                (int) (this.r * MODIFIERS[i]),
                (int) (this.g * MODIFIERS[i]),
                (int) (this.b * MODIFIERS[i])
        );
    }

    public Color getVariant0() {
        return this.getVariant(0);
    }

    public Color getVariant1() {
        return this.getVariant(1);
    }

    public Color getVariant2() {
        return this.getVariant(2);
    }

    public Color getVariant3() {
        return this.getVariant(3);
    }

    public int getId() {
        return this.id;
    }

    public int getR() {
        return this.r;
    }

    public int getG() {
        return this.g;
    }

    public int getB() {
        return this.b;
    }

}
