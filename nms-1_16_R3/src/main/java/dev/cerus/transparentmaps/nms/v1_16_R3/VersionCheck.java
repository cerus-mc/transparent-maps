package dev.cerus.transparentmaps.nms.v1_16_R3;

public class VersionCheck {

    public static boolean check(final int major, final int minor, final Integer patch) {
        return major == 1 && minor == 16 && patch != null && patch == 5;
    }

}
