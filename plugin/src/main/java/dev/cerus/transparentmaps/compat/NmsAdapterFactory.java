package dev.cerus.transparentmaps.compat;

import dev.cerus.transparentmaps.nms.NmsAdapter;
import org.bukkit.Bukkit;

public class NmsAdapterFactory {

    public static NmsAdapter getAdapter() {
        String ver = Bukkit.getVersion().split(":")[1];
        ver = ver.substring(0, ver.length() - 1).trim();

        switch (ver) {
            case "1.16.5":
                return new dev.cerus.transparentmaps.nms.v1_16_R3.NmsAdapterImpl();
            case "1.17":
            case "1.17.1":
                return new dev.cerus.transparentmaps.nms.v1_17_R1.NmsAdapterImpl();
            case "1.18":
            case "1.18.1":
                return new dev.cerus.transparentmaps.nms.v1_18_R1.NmsAdapterImpl();
            case "1.18.2":
                return new dev.cerus.transparentmaps.nms.v1_18_R2.NmsAdapterImpl();
            case "1.19":
            case "1.19.1":
            case "1.19.2":
                return new dev.cerus.transparentmaps.nms.v1_19_R1.NmsAdapterImpl();
            case "1.19.3":
                return new dev.cerus.transparentmaps.nms.v1_19_R2.NmsAdapterImpl();
            case "1.19.4":
                return new dev.cerus.transparentmaps.nms.v1_19_R3.NmsAdapterImpl();
            case "1.20":
            case "1.20.1":
                return new dev.cerus.transparentmaps.nms.v1_20_R1.NmsAdapterImpl();
            default:
                return null;
        }
    }

}
