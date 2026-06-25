package com.yesithx.placeholdertebex.manager;

import com.yesithx.placeholdertebex.PlaceholderTebex;

public class ConfigManager {

    private final PlaceholderTebex plugin;

    public ConfigManager(PlaceholderTebex plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public String getSecretKey() {
        return plugin.getConfig().getString("tebex.secret-key", "");
    }

    public String getBaseUrl() {
        return plugin.getConfig().getString("tebex.base-url", "https://plugin.tebex.io");
    }

    public int getCacheRefreshInterval() {
        return plugin.getConfig().getInt("cache.refresh-interval", 10);
    }

    public int getPaymentHistoryLimit() {
        return plugin.getConfig().getInt("cache.payment-history-limit", 100);
    }

    public boolean isGoalEnabled() {
        return plugin.getConfig().getBoolean("goal.enabled", true);
    }

    public double getGoalTarget() {
        return plugin.getConfig().getDouble("goal.target-amount", 500.0);
    }

    public String getNoDataPlaceholder() {
        return plugin.getConfig().getString("placeholders.no-data", "N/A");
    }

    public String getLoadingPlaceholder() {
        return plugin.getConfig().getString("placeholders.loading", "Loading...");
    }

    public String getCurrencyFormat() {
        return plugin.getConfig().getString("placeholders.currency-format", "%.2f");
    }

    public String getDateFormat() {
        return plugin.getConfig().getString("placeholders.date-format", "dd/MM/yyyy");
    }

    public String getDateTimeFormat() {
        return plugin.getConfig().getString("placeholders.datetime-format", "dd/MM/yyyy HH:mm");
    }

    public String getMessage(String key) {
        String msg = plugin.getConfig().getString("messages." + key, "&cMessage not found.");
        return msg.replace("&", "\u00a7");
    }
}
