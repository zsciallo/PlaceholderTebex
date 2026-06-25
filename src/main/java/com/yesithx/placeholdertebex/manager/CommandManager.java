package com.yesithx.placeholdertebex.manager;

import com.yesithx.placeholdertebex.PlaceholderTebex;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final PlaceholderTebex plugin;

    public CommandManager(PlaceholderTebex plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("placeholdertebex.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.getConfigManager().reload();
                plugin.getCacheManager().stop();
                plugin.getCacheManager().start();
                sender.sendMessage(plugin.getConfigManager().getMessage("reload-success"));
            }
            case "refresh" -> {
                sender.sendMessage(plugin.getConfigManager().getMessage("fetching"));
                plugin.getCacheManager().forceRefresh();
                sender.sendMessage(plugin.getConfigManager().getMessage("fetched"));
            }
            case "clearcache" -> {
                plugin.getCacheManager().clearCache();
                sender.sendMessage(plugin.getConfigManager().getMessage("cache-cleared"));
            }
            case "info" -> sendInfo(sender);
            default -> sendHelp(sender, label);
        }

        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage("\u00a7e\u00a7lPlaceholderTebex \u00a77Commands:");
        sender.sendMessage("\u00a7e/" + label + " reload \u00a77- Reload config and restart cache task.");
        sender.sendMessage("\u00a7e/" + label + " refresh \u00a77- Force fetch data from Tebex now.");
        sender.sendMessage("\u00a7e/" + label + " clearcache \u00a77- Clear all cached data.");
        sender.sendMessage("\u00a7e/" + label + " info \u00a77- Show plugin status info.");
    }

    private void sendInfo(CommandSender sender) {
        sender.sendMessage("\u00a7e\u00a7lPlaceholderTebex Info");
        sender.sendMessage("\u00a77Version: \u00a7f" + plugin.getDescription().getVersion());
        sender.sendMessage("\u00a77Cache loaded: \u00a7f" + plugin.getCacheManager().isLoaded());
        sender.sendMessage("\u00a77Refresh interval: \u00a7f" + plugin.getConfigManager().getCacheRefreshInterval() + " min");
        sender.sendMessage("\u00a77Cached payments: \u00a7f" + plugin.getCacheManager().getRecentPayments().size());
        sender.sendMessage("\u00a77Unique donors: \u00a7f" + plugin.getCacheManager().getUniqueDonors());
        sender.sendMessage("\u00a77Goal enabled: \u00a7f" + plugin.getConfigManager().isGoalEnabled());
        sender.sendMessage("\u00a77Goal target: \u00a7f" + plugin.getConfigManager().getGoalTarget());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "refresh", "clearcache", "info");
        }
        return List.of();
    }
}
