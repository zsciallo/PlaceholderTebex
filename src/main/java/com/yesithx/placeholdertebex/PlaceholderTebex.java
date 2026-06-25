package com.yesithx.placeholdertebex;

import com.yesithx.placeholdertebex.api.TebexApiClient;
import com.yesithx.placeholdertebex.cache.CacheManager;
import com.yesithx.placeholdertebex.expansion.TebexExpansion;
import com.yesithx.placeholdertebex.manager.CommandManager;
import com.yesithx.placeholdertebex.manager.ConfigManager;
import com.yesithx.placeholdertebex.util.FormatUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlaceholderTebex extends JavaPlugin {

    private ConfigManager configManager;
    private TebexApiClient apiClient;
    private CacheManager cacheManager;
    private FormatUtil formatUtil;
    private TebexExpansion expansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        apiClient = new TebexApiClient(this);
        cacheManager = new CacheManager(this);
        formatUtil = new FormatUtil(this);

        cacheManager.start();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            expansion = new TebexExpansion(this);
            expansion.register();
            getLogger().info("PlaceholderAPI expansion registered successfully.");
        } else {
            getLogger().severe("PlaceholderAPI not found! Placeholders will not work.");
        }

        CommandManager cmdManager = new CommandManager(this);
        getCommand("placeholdertebex").setExecutor(cmdManager);
        getCommand("placeholdertebex").setTabCompleter(cmdManager);

        getLogger().info("PlaceholderTebex v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (cacheManager != null) {
            cacheManager.stop();
        }
        if (expansion != null) {
            expansion.unregister();
        }
        getLogger().info("PlaceholderTebex disabled.");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public TebexApiClient getApiClient() { return apiClient; }
    public CacheManager getCacheManager() { return cacheManager; }
    public FormatUtil getFormatUtil() { return formatUtil; }
}
