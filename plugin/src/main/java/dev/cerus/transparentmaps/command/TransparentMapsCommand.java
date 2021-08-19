package dev.cerus.transparentmaps.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.cerus.transparentmaps.TransparentMapsPlugin;
import dev.cerus.transparentmaps.misc.EconomyContext;
import dev.cerus.transparentmaps.nms.NmsAdapter;
import dev.cerus.transparentmaps.util.MapColorUtil;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpiringMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

@CommandAlias("transparentmaps")
@CommandPermission("transparentmaps.command")
public class TransparentMapsCommand extends BaseCommand {

    private final ExpiringMap<UUID, Integer> allowedToReplaceMap = ExpiringMap.builder()
            .expiration(2, TimeUnit.MINUTES)
            .build();

    @Dependency
    private NmsAdapter adapter;

    @Dependency
    private EconomyContext economyContext;

    @Default
    public void handle(final Player player) {
        final BlockData blockData;
        try {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                // Incredibly dirty hack. Don't do this.
                throw new IllegalArgumentException();
            }
            blockData = player.getInventory().getItemInOffHand().getType().createBlockData();
        } catch (final IllegalArgumentException | NullPointerException ignored) {
            player.sendMessage(TransparentMapsPlugin.Messages.HOLD_BLOCK_OFFHAND);
            return;
        }

        final int colorId = this.adapter.getBlockBaseColor(blockData) / 4;
        if (colorId == 0) {
            player.sendMessage(TransparentMapsPlugin.Messages.BLOCK_COLOR);
            return;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() != Material.FILLED_MAP) {
            player.sendMessage(TransparentMapsPlugin.Messages.HOLD_OPEN_MAP);
            return;
        }

        final MapMeta meta = (MapMeta) itemStack.getItemMeta();
        if (meta == null || meta.getMapView() == null) {
            player.sendMessage(TransparentMapsPlugin.Messages.HOLD_OPEN_MAP);
            return;
        }

        this.allowedToReplaceMap.put(player.getUniqueId(), meta.getMapView().getId());

        final int rgb = this.adapter.getBlockRgbColor(blockData);
        player.sendMessage(TransparentMapsPlugin.Messages.CLICK_SHADE);
        if (this.economyContext.isEnabled()) {
            player.sendMessage(String.format(TransparentMapsPlugin.Messages.ECO_PRICE,
                    this.economyContext.getEconomy().format(this.economyContext.getPrice()),
                    this.economyContext.getEconomy().currencyNamePlural()));
        }
        player.spigot().sendMessage(new ComponentBuilder()
                .append(new ComponentBuilder("█")
                        .color(ChatColor.of("#" + MapColorUtil.modRgbToHex(rgb, 0)))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/transparentmaps replace " + (colorId * 4)))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7§oSelect variant '1'")))
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .append(new ComponentBuilder("   ")
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .append(new ComponentBuilder("█")
                        .color(ChatColor.of("#" + MapColorUtil.modRgbToHex(rgb, 1)))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/transparentmaps replace " + (colorId * 4 + 1)))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7§oSelect variant '2'")))
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .append(new ComponentBuilder("   ")
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .append(new ComponentBuilder("█")
                        .color(ChatColor.of("#" + MapColorUtil.modRgbToHex(rgb, 2)))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/transparentmaps replace " + (colorId * 4 + 2)))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7§oSelect variant '3'")))
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .append(new ComponentBuilder("   ")
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .append(new ComponentBuilder("█")
                        .color(ChatColor.of("#" + MapColorUtil.modRgbToHex(rgb, 3)))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/transparentmaps replace " + (colorId * 4 + 3)))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7§oSelect variant '4'")))
                        .create(), ComponentBuilder.FormatRetention.NONE)
                .create());

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    @Subcommand("replace")
    public void handleReplace(final Player player, final int color) {
        if (color < 4 || color > 255) {
            return;
        }

        if (!this.allowedToReplaceMap.containsKey(player.getUniqueId())) {
            return;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() != Material.FILLED_MAP) {
            return;
        }

        final MapMeta meta = (MapMeta) itemStack.getItemMeta();
        if (meta == null || meta.getMapView() == null) {
            return;
        }

        final MapView mapView = meta.getMapView();
        if (mapView.getId() != this.allowedToReplaceMap.get(player.getUniqueId())) {
            return;
        }

        if (this.economyContext.isEnabled()) {
            if (!this.handleEco(player)) {
                return;
            }
        }

        this.allowedToReplaceMap.resetExpiration(player.getUniqueId());

        final byte[] data = this.adapter.getWorldMapData(itemStack, mapView.getWorld());
        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                final byte b = data[x + z * 128];
                if (b == (byte) color) {
                    data[x + z * 128] = (byte) 0;
                }
            }
        }

        this.adapter.saveWorldMap(itemStack, mapView.getWorld());
        player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1, 1);
    }

    private boolean handleEco(final Player player) {
        final Economy economy = this.economyContext.getEconomy();
        if (!economy.has(player, this.economyContext.getPrice())) {
            player.sendMessage(TransparentMapsPlugin.Messages.ECO_NO_MONEY);
            return false;
        }

        final EconomyResponse response = economy.withdrawPlayer(player, this.economyContext.getPrice());
        if (!response.transactionSuccess()) {
            System.err.println("Failed to withdraw money: " + response.errorMessage);
            player.sendMessage(TransparentMapsPlugin.Messages.ECO_ERROR);
            return false;
        }
        return true;
    }

}
