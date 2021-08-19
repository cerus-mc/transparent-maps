package dev.cerus.transparentmaps.nms.v1_17_R1;

import dev.cerus.transparentmaps.nms.NmsAdapter;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.level.material.MaterialMapColor;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NmsAdapterImpl implements NmsAdapter {

    @Override
    public int getBlockBaseColor(final BlockData blockData) {
        final MaterialMapColor mapColor = ((CraftBlockData) blockData).getState().d(null, null);
        return mapColor.am * 4;
    }

    @Override
    public int getBlockRgbColor(final BlockData blockData) {
        final MaterialMapColor mapColor = ((CraftBlockData) blockData).getState().d(null, null);
        return mapColor.al;
    }

    @Override
    public void saveWorldMap(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        WorldMap worldMap = ItemWorldMap.getSavedMap(nmsStack, worldServer);
        if (worldMap == null) {
            final int id = ItemWorldMap.createNewSavedData(worldServer, worldServer.E.a(), worldServer.E.c(), 3, false, false, worldServer.getDimensionKey());
            nmsStack.getOrCreateTag().set("map", NBTTagInt.a(id));
            worldMap = ItemWorldMap.getSavedMap(nmsStack, worldServer);
        }
        worldServer.a("map_" + nmsStack.getOrCreateTag().getInt("map"), worldMap);
        worldMap.h = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public byte[] getWorldMapData(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = ItemWorldMap.getSavedMap(CraftItemStack.asNMSCopy(itemStack), worldServer);
        return worldMap.g;
    }

    @Override
    public boolean acceptsVersion(final int major, final int minor, final Integer patch) {
        return major == 1 && minor == 17;
    }

}
