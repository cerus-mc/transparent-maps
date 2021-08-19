package dev.cerus.transparentmaps;

import co.aikar.commands.BukkitCommandManager;
import dev.cerus.transparentmaps.command.TransparentMapsCommand;
import dev.cerus.transparentmaps.compat.NmsAdapterFactory;
import dev.cerus.transparentmaps.misc.EconomyContext;
import dev.cerus.transparentmaps.nms.NmsAdapter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TransparentMapsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        final FileConfiguration config = this.getConfig();
        Messages.HOLD_BLOCK_OFFHAND = this.color(config.getString("messages.hold-block-offhand", Messages.UNKNOWN));
        Messages.BLOCK_COLOR = this.color(config.getString("messages.block-color", Messages.UNKNOWN));
        Messages.HOLD_OPEN_MAP = this.color(config.getString("messages.hold-open-map", Messages.UNKNOWN));
        Messages.CLICK_SHADE = this.color(config.getString("messages.click-shade", Messages.UNKNOWN));
        Messages.ECO_PRICE = this.color(config.getString("messages.eco-price", Messages.UNKNOWN));
        Messages.ECO_NO_MONEY = this.color(config.getString("messages.eco-no-money", Messages.UNKNOWN));
        Messages.ECO_ERROR = this.color(config.getString("messages.eco-error", Messages.UNKNOWN));

        boolean enableEco = config.getBoolean("economy.enable");
        final double price = config.getDouble("economy.price");
        final EconomyContext economyContext;

        final Metrics metrics = new Metrics(this, 12499);
        metrics.addCustomChart(new SimplePie("eco_enabled", () -> config.getBoolean("economy.enable") ? "Yes" : "No"));

        if (enableEco && !this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            enableEco = false;
            this.getLogger().warning("Can't enable economy support because Vault is not installed");
        }

        economyContext = enableEco
                ? EconomyContext.create(true, price, this.getServer())
                : EconomyContext.createEmpty();

        final NmsAdapter adapter = NmsAdapterFactory.getAdapter();
        if (adapter == null) {
            this.getLogger().warning("Incompatible server version");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        final BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.registerDependency(NmsAdapter.class, adapter);
        commandManager.registerDependency(EconomyContext.class, economyContext);
        commandManager.registerCommand(new TransparentMapsCommand());
    }

    private String color(final String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static class Messages {

        public static String UNKNOWN = "§cUnknown message";
        public static String HOLD_BLOCK_OFFHAND = UNKNOWN;
        public static String BLOCK_COLOR = UNKNOWN;
        public static String HOLD_OPEN_MAP = UNKNOWN;
        public static String CLICK_SHADE = UNKNOWN;
        public static String ECO_PRICE = UNKNOWN;
        public static String ECO_NO_MONEY = UNKNOWN;
        public static String ECO_ERROR = UNKNOWN;

    }

}
