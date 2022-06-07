package dev.cerus.transparentmaps.nms.v1_19_R1;

import dev.cerus.transparentmaps.nms.NmsAdapter;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.level.material.MaterialMapColor;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NmsAdapterImpl implements NmsAdapter {

    @Override
    public int getBlockBaseColor(final BlockData blockData) {
        final MaterialMapColor mapColor = ((CraftBlockData) blockData).getState().d(null, null);
        return mapColor.al * 4;
    }

    @Override
    public int getBlockRgbColor(final BlockData blockData) {
        final MaterialMapColor mapColor = ((CraftBlockData) blockData).getState().d(null, null);
        return mapColor.ak;
    }

    @Override
    public void saveWorldMap(final int mapId, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = worldServer.a("map_" + mapId);
        worldServer.a("map_" + mapId, worldMap);
        worldMap.h = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public void saveWorldMap(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        WorldMap worldMap = ItemWorldMap.a(nmsStack, worldServer);
        if (worldMap == null) {
            final int id = ItemWorldMap.a(worldServer, worldServer.N.a(), worldServer.N.c(), 3, false, false, worldServer.getMinecraftWorld().ab());
            nmsStack.v().a("map", NBTTagInt.a(id));
            worldMap = ItemWorldMap.a(nmsStack, worldServer);
        }
        worldServer.a("map_" + nmsStack.v().h("map"), worldMap);
        worldMap.h = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public byte[] getWorldMapData(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = ItemWorldMap.a(CraftItemStack.asNMSCopy(itemStack), worldServer);
        return worldMap.g;
    }

    @Override
    public byte[] getWorldMapData(final int mapId, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = worldServer.a("map_" + mapId);
        return worldMap.g;
    }

    @Override
    public boolean acceptsVersion(final int major, final int minor, final Integer patch) {
        return major == 1 && minor == 18;
    }

}
