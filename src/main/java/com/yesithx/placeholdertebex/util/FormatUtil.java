package com.yesithx.placeholdertebex.util;

import com.yesithx.placeholdertebex.PlaceholderTebex;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtil {

    private final PlaceholderTebex plugin;

    public FormatUtil(PlaceholderTebex plugin) {
        this.plugin = plugin;
    }

    public String formatAmount(double amount) {
        return String.format(plugin.getConfigManager().getCurrencyFormat(), amount);
    }

    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return plugin.getConfigManager().getNoDataPlaceholder();
        return dateTime.format(DateTimeFormatter.ofPattern(plugin.getConfigManager().getDateFormat()));
    }

    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return plugin.getConfigManager().getNoDataPlaceholder();
        return dateTime.format(DateTimeFormatter.ofPattern(plugin.getConfigManager().getDateTimeFormat()));
    }

    public String formatGoalProgress(double current, double target) {
        if (target <= 0) return "0.00";
        double pct = (current / target) * 100.0;
        return String.format("%.2f", Math.min(pct, 100.0));
    }

    public String formatGoalBar(double current, double target, int length) {
        if (target <= 0) return "▓".repeat(0) + "░".repeat(length);
        double pct = Math.min(current / target, 1.0);
        int filled = (int) Math.round(pct * length);
        return "▓".repeat(filled) + "░".repeat(length - filled);
    }

    public String noData() {
        return plugin.getConfigManager().getNoDataPlaceholder();
    }

    public String loading() {
        return plugin.getConfigManager().getLoadingPlaceholder();
    }
}
