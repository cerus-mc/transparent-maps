package dev.cerus.transparentmaps.nms.v1_21_R1;

import dev.cerus.transparentmaps.nms.NmsAdapter;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.level.material.MaterialMapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
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
    public void saveWorldMap(final int mapIdNum, final World world) {
        final MapId mapId = new MapId(mapIdNum);
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = worldServer.a(mapId);
        worldServer.a(mapId, worldMap);
        worldMap.h = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public void saveWorldMap(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        WorldMap worldMap = ItemWorldMap.b(nmsStack, worldServer);
        if (worldMap == null) {
            // See CraftServer
            BlockPosition spawn = worldServer.A_().a();
            MapId newId = ItemWorldMap.a(worldServer, spawn.u(), spawn.w(), 3, false, false, worldServer.af());
            nmsStack.b(DataComponents.B, newId);
            worldMap = ItemWorldMap.b(nmsStack, worldServer);
        }
        worldServer.a(nmsStack.a(DataComponents.B), worldMap);
        worldMap.h = true;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMap(worldMap.mapView);
        }
    }

    @Override
    public byte[] getWorldMapData(final ItemStack itemStack, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = ItemWorldMap.b(CraftItemStack.asNMSCopy(itemStack), worldServer);
        return worldMap.g;
    }

    @Override
    public byte[] getWorldMapData(final int mapId, final World world) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final WorldMap worldMap = worldServer.a(new MapId(mapId));
        return worldMap.g;
    }

    @Override
    public boolean acceptsVersion(final int major, final int minor, final Integer patch) {
        return major == 1 && minor == 21;
    }

}
