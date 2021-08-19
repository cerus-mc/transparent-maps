package dev.cerus.transparentmaps.misc;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;

public class EconomyContext {

    private final boolean enable;
    private final double price;
    private final Economy economy;

    public EconomyContext(final boolean enable, final double price, final Economy economy) {
        this.enable = enable;
        this.price = price;
        this.economy = economy;
    }

    public static EconomyContext create(final boolean enable, final double price, final Server server) {
        return new EconomyContext(enable, price, server.getServicesManager().load(Economy.class));
    }

    public static EconomyContext createEmpty() {
        return new EconomyContext(false, 0, null);
    }

    public boolean isEnabled() {
        return this.enable && this.price > 0;
    }

    public double getPrice() {
        return this.price;
    }

    public Economy getEconomy() {
        return this.economy;
    }

}
