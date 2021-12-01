package dev.cerus.transparentmaps.nms.v1_16_R3;

import dev.cerus.transparentmaps.nms.NmsAdapter;
import net.minecraft.server.v1_16_R3.ItemWorldMap;
import net.minecraft.server.v1_16_R3.MaterialMapColor;
import net.minecraft.server.v1_16_R3.WorldMap;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NmsAdapterImpl implements NmsAdapter {

    @Override
    public int getBlockBaseColor(final BlockData blockData) {
        final MaterialMapColor mapColor = ((CraftBlockData) blockData).getState().d(null, null);
        return mapColor.aj * 4;
    }

    @Override
    public int getBlockRgbColor(final BlockData blockData) {
        final MaterialMapColor mapColor = ((CraftBlockData) blockData).getState().d(null, null);
        return mapColor.rgb;
    }

    @Override
    public void saveWorldMap(final int mapId, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = worldServer.a("map_" + mapId);
        worldServer.a(worldMap);
        worldMap.locked = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public void saveWorldMap(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = ItemWorldMap.getSavedMap(CraftItemStack.asNMSCopy(itemStack), worldServer);
        worldServer.a(worldMap);
        worldMap.locked = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public byte[] getWorldMapData(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = ItemWorldMap.getSavedMap(CraftItemStack.asNMSCopy(itemStack), worldServer);
        return worldMap.colors;
    }

    @Override
    public byte[] getWorldMapData(final int mapId, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = worldServer.a("map_" + mapId);
        return worldMap.colors;
    }

    @Override
    public boolean acceptsVersion(final int major, final int minor, final Integer patch) {
        return major == 1 && minor == 16 && patch != null && patch == 5;
    }

}
