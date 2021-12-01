package dev.cerus.transparentmaps.compat;

import dev.cerus.transparentmaps.nms.NmsAdapter;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.Bukkit;

public class NmsAdapterFactory {

    private static final NmsAdapter[] ADAPTERS = new NmsAdapter[3];

    private static final int major;
    private static final int minor;
    private static final Integer patch;

    static {
        String ver = Bukkit.getVersion().split(":")[1];
        ver = ver.substring(0, ver.length() - 1).trim();

        if (!ver.matches("\\d+\\.\\d+(\\.\\d+)?")) {
            throw new IllegalStateException("Modified version string");
        }

        final String[] split = ver.split("\\.");
        major = Integer.parseInt(split[0]);
        minor = Integer.parseInt(split[1]);
        patch = split.length > 2 ? Integer.parseInt(split[2]) : null;

        if (dev.cerus.transparentmaps.nms.v1_16_R3.VersionCheck.check(major, minor, patch)) {
            ADAPTERS[0] = new dev.cerus.transparentmaps.nms.v1_16_R3.NmsAdapterImpl();
        }

        // Because MC 1.17+ uses Java 16/17 we need this little workaround
        // for servers running an older Java version
        try {
            if (dev.cerus.transparentmaps.nms.v1_17_R1.VersionCheck.check(major, minor, patch)) {
                ADAPTERS[1] = new dev.cerus.transparentmaps.nms.v1_17_R1.NmsAdapterImpl();
            }
        } catch (final UnsupportedClassVersionError ignored) {
        }
        try {
            if (dev.cerus.transparentmaps.nms.v1_18_R1.VersionCheck.check(major, minor, patch)) {
                ADAPTERS[2] = new dev.cerus.transparentmaps.nms.v1_18_R1.NmsAdapterImpl();
            }
        } catch (final UnsupportedClassVersionError ignored) {
        }
    }

    public static NmsAdapter getAdapter() {
        return Arrays.stream(ADAPTERS)
                .filter(Objects::nonNull)
                .filter(nmsAdapter -> nmsAdapter.acceptsVersion(major, minor, patch))
                .findAny()
                .orElse(null);
    }

}
