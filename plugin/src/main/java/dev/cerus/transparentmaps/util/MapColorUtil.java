package dev.cerus.transparentmaps.util;

import java.awt.Color;

public class MapColorUtil {

    private MapColorUtil() {
    }

    public static String modRgbToHex(final int rgb, final int variant) {
        final double[] modifiers = new double[] {0.71, 0.86, 1, 0.53};
        final double mod = modifiers[variant];

        final Color color = new Color(rgb);
        final int r = (int) (color.getRed() * mod);
        final int g = (int) (color.getGreen() * mod);
        final int b = (int) (color.getBlue() * mod);
        return String.format("%02x%02x%02x", r, g, b);
    }

}
