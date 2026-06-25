package com.yesithx.placeholdertebex.expansion;

import com.yesithx.placeholdertebex.PlaceholderTebex;
import com.yesithx.placeholdertebex.cache.CacheManager;
import com.yesithx.placeholdertebex.model.Donor;
import com.yesithx.placeholdertebex.model.StoreInfo;
import com.yesithx.placeholdertebex.util.FormatUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TebexExpansion extends PlaceholderExpansion {

    private final PlaceholderTebex plugin;
    private final CacheManager cache;
    private final FormatUtil format;

    public TebexExpansion(PlaceholderTebex plugin) {
        this.plugin = plugin;
        this.cache = plugin.getCacheManager();
        this.format = plugin.getFormatUtil();
    }

    @Override
    public @NotNull String getIdentifier() { return "tebex"; }

    @Override
    public @NotNull String getAuthor() { return "YesithX"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!cache.isLoaded()) return format.loading();

        if (params.equals("last_donor_name")) return getLastDonorName();
        if (params.equals("last_donor_amount")) return getLastDonorAmount();
        if (params.equals("last_donor_currency")) return getLastDonorCurrency();
        if (params.equals("last_donor_date")) return getLastDonorDate();
        if (params.equals("last_donor_datetime")) return getLastDonorDateTime();
        if (params.equals("last_donor_package")) return getLastDonorPackage();
        if (params.equals("last_donor_transaction")) return getLastDonorTransaction();

        if (params.equals("total_donated")) return format.formatAmount(cache.getTotalDonated());
        if (params.equals("total_donated_month")) return format.formatAmount(cache.getTotalDonatedThisMonth());
        if (params.equals("total_donated_today")) return format.formatAmount(cache.getTotalDonatedToday());
        if (params.equals("total_transactions")) return String.valueOf(cache.getTotalTransactions());
        if (params.equals("total_transactions_month")) return String.valueOf(cache.getTransactionsThisMonth());
        if (params.equals("total_transactions_today")) return String.valueOf(cache.getTransactionsToday());
        if (params.equals("unique_donors")) return String.valueOf(cache.getUniqueDonors());

        if (params.equals("goal_target")) return format.formatAmount(plugin.getConfigManager().getGoalTarget());
        if (params.equals("goal_current")) return format.formatAmount(cache.getTotalDonatedThisMonth());
        if (params.equals("goal_progress")) return format.formatGoalProgress(cache.getTotalDonatedThisMonth(), plugin.getConfigManager().getGoalTarget()) + "%";
        if (params.equals("goal_progress_raw")) return format.formatGoalProgress(cache.getTotalDonatedThisMonth(), plugin.getConfigManager().getGoalTarget());
        if (params.equals("goal_bar")) return format.formatGoalBar(cache.getTotalDonatedThisMonth(), plugin.getConfigManager().getGoalTarget(), 20);
        if (params.equals("goal_bar_10")) return format.formatGoalBar(cache.getTotalDonatedThisMonth(), plugin.getConfigManager().getGoalTarget(), 10);
        if (params.equals("goal_remaining")) return format.formatAmount(Math.max(0, plugin.getConfigManager().getGoalTarget() - cache.getTotalDonatedThisMonth()));
        if (params.equals("goal_reached")) return cache.getTotalDonatedThisMonth() >= plugin.getConfigManager().getGoalTarget() ? "true" : "false";

        if (params.equals("store_name")) return getStoreName();
        if (params.equals("store_currency")) return getStoreCurrency();
        if (params.equals("store_domain")) return getStoreDomain();

        if (params.startsWith("top_name_")) return getTopName(params.substring(9), false);
        if (params.startsWith("top_amount_")) return getTopAmount(params.substring(11), false);
        if (params.startsWith("top_currency_")) return getTopCurrency(params.substring(13), false);
        if (params.startsWith("top_date_")) return getTopDate(params.substring(9), false);
        if (params.startsWith("top_package_")) return getTopPackage(params.substring(12), false);

        if (params.startsWith("monthly_top_name_")) return getTopName(params.substring(17), true);
        if (params.startsWith("monthly_top_amount_")) return getTopAmount(params.substring(19), true);
        if (params.startsWith("monthly_top_currency_")) return getTopCurrency(params.substring(21), true);
        if (params.startsWith("monthly_top_date_")) return getTopDate(params.substring(17), true);
        if (params.startsWith("monthly_top_package_")) return getTopPackage(params.substring(20), true);

        if (params.equals("monthly_total")) return format.formatAmount(cache.getTotalDonatedThisMonth());
        if (params.equals("monthly_transactions")) return String.valueOf(cache.getTransactionsThisMonth());

        return null;
    }

    private String getLastDonorName() {
        Donor d = cache.getLastDonor();
        return d != null ? d.getUsername() : format.noData();
    }

    private String getLastDonorAmount() {
        Donor d = cache.getLastDonor();
        return d != null ? format.formatAmount(d.getAmount()) : format.noData();
    }

    private String getLastDonorCurrency() {
        Donor d = cache.getLastDonor();
        return d != null ? d.getCurrency() : format.noData();
    }

    private String getLastDonorDate() {
        Donor d = cache.getLastDonor();
        return d != null ? format.formatDate(d.getDate()) : format.noData();
    }

    private String getLastDonorDateTime() {
        Donor d = cache.getLastDonor();
        return d != null ? format.formatDateTime(d.getDate()) : format.noData();
    }

    private String getLastDonorPackage() {
        Donor d = cache.getLastDonor();
        return d != null ? d.getPackageName() : format.noData();
    }

    private String getLastDonorTransaction() {
        Donor d = cache.getLastDonor();
        return d != null ? d.getTransactionId() : format.noData();
    }

    private String getStoreName() {
        StoreInfo info = cache.getStoreInfo();
        return info != null ? info.getStoreName() : format.noData();
    }

    private String getStoreCurrency() {
        StoreInfo info = cache.getStoreInfo();
        return info != null ? info.getCurrency() : format.noData();
    }

    private String getStoreDomain() {
        StoreInfo info = cache.getStoreInfo();
        return info != null ? info.getDomain() : format.noData();
    }

    private int parsePosition(String raw) {
        try {
            int pos = Integer.parseInt(raw);
            return (pos >= 1 && pos <= 10) ? pos : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String getTopName(String raw, boolean monthly) {
        int pos = parsePosition(raw);
        if (pos == -1) return format.noData();
        Donor d = monthly ? cache.getMonthlyTopDonor(pos) : cache.getTopDonor(pos);
        return d != null ? d.getUsername() : format.noData();
    }

    private String getTopAmount(String raw, boolean monthly) {
        int pos = parsePosition(raw);
        if (pos == -1) return format.noData();
        Donor d = monthly ? cache.getMonthlyTopDonor(pos) : cache.getTopDonor(pos);
        return d != null ? format.formatAmount(d.getAmount()) : format.noData();
    }

    private String getTopCurrency(String raw, boolean monthly) {
        int pos = parsePosition(raw);
        if (pos == -1) return format.noData();
        Donor d = monthly ? cache.getMonthlyTopDonor(pos) : cache.getTopDonor(pos);
        return d != null ? d.getCurrency() : format.noData();
    }

    private String getTopDate(String raw, boolean monthly) {
        int pos = parsePosition(raw);
        if (pos == -1) return format.noData();
        Donor d = monthly ? cache.getMonthlyTopDonor(pos) : cache.getTopDonor(pos);
        return d != null ? format.formatDate(d.getDate()) : format.noData();
    }

    private String getTopPackage(String raw, boolean monthly) {
        int pos = parsePosition(raw);
        if (pos == -1) return format.noData();
        Donor d = monthly ? cache.getMonthlyTopDonor(pos) : cache.getTopDonor(pos);
        return d != null ? d.getPackageName() : format.noData();
    }
}
