package com.yesithx.placeholdertebex.cache;

import com.yesithx.placeholdertebex.PlaceholderTebex;
import com.yesithx.placeholdertebex.model.Donor;
import com.yesithx.placeholdertebex.model.StoreInfo;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CacheManager {

    private final PlaceholderTebex plugin;
    private BukkitTask refreshTask;

    private List<Donor> recentPayments = new ArrayList<>();
    private List<Donor> topDonors = new ArrayList<>();
    private List<Donor> monthlyDonors = new ArrayList<>();
    private StoreInfo storeInfo = null;
    private boolean loaded = false;

    public CacheManager(PlaceholderTebex plugin) {
        this.plugin = plugin;
    }

    public void start() {
        long intervalTicks = plugin.getConfigManager().getCacheRefreshInterval() * 60L * 20L;
        refreshTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::refresh, 0L, intervalTicks);
    }

    public void stop() {
        if (refreshTask != null && !refreshTask.isCancelled()) {
            refreshTask.cancel();
        }
    }

    public void forceRefresh() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::refresh);
    }

    public void clearCache() {
        recentPayments = new ArrayList<>();
        topDonors = new ArrayList<>();
        monthlyDonors = new ArrayList<>();
        storeInfo = null;
        loaded = false;
    }

    private void refresh() {
        try {
            int limit = plugin.getConfigManager().getPaymentHistoryLimit();
            List<Donor> payments = plugin.getApiClient().fetchPayments(limit);
            StoreInfo info = plugin.getApiClient().fetchStoreInfo();

            recentPayments = new ArrayList<>(payments);
            storeInfo = info;

            topDonors = buildTopDonors(payments);
            monthlyDonors = buildMonthlyDonors(payments);

            loaded = true;
        } catch (Exception e) {
            plugin.getLogger().warning("[PlaceholderTebex] Cache refresh failed: " + e.getMessage());
        }
    }

    private List<Donor> buildTopDonors(List<Donor> payments) {
        Map<String, Double> totals = new TreeMap<>();
        Map<String, Donor> lastPayment = new TreeMap<>();

        for (Donor d : payments) {
            totals.merge(d.getUsername(), d.getAmount(), Double::sum);
            lastPayment.putIfAbsent(d.getUsername(), d);
        }

        return totals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .map(e -> {
                    Donor original = lastPayment.get(e.getKey());
                    return new Donor(
                            e.getKey(),
                            e.getValue(),
                            original.getCurrency(),
                            original.getDate(),
                            original.getPackageName(),
                            original.getTransactionId(),
                            original.getEmail()
                    );
                })
                .collect(Collectors.toList());
    }

    private List<Donor> buildMonthlyDonors(List<Donor> payments) {
        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        List<Donor> filtered = payments.stream()
                .filter(d -> d.getDate().getMonthValue() == currentMonth
                        && d.getDate().getYear() == currentYear)
                .collect(Collectors.toList());

        return buildTopDonors(filtered);
    }

    public List<Donor> getRecentPayments() { return Collections.unmodifiableList(recentPayments); }
    public List<Donor> getTopDonors() { return Collections.unmodifiableList(topDonors); }
    public List<Donor> getMonthlyDonors() { return Collections.unmodifiableList(monthlyDonors); }
    public StoreInfo getStoreInfo() { return storeInfo; }
    public boolean isLoaded() { return loaded; }

    public Donor getLastDonor() {
        return recentPayments.isEmpty() ? null : recentPayments.get(0);
    }

    public Donor getTopDonor(int position) {
        if (position < 1 || position > topDonors.size()) return null;
        return topDonors.get(position - 1);
    }

    public Donor getMonthlyTopDonor(int position) {
        if (position < 1 || position > monthlyDonors.size()) return null;
        return monthlyDonors.get(position - 1);
    }

    public double getTotalDonated() {
        return recentPayments.stream().mapToDouble(Donor::getAmount).sum();
    }

    public double getTotalDonatedThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        return recentPayments.stream()
                .filter(d -> d.getDate().getMonthValue() == now.getMonthValue()
                        && d.getDate().getYear() == now.getYear())
                .mapToDouble(Donor::getAmount)
                .sum();
    }

    public double getTotalDonatedToday() {
        LocalDateTime now = LocalDateTime.now();
        return recentPayments.stream()
                .filter(d -> d.getDate().toLocalDate().equals(now.toLocalDate()))
                .mapToDouble(Donor::getAmount)
                .sum();
    }

    public long getTotalTransactions() {
        return recentPayments.size();
    }

    public long getTransactionsThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        return recentPayments.stream()
                .filter(d -> d.getDate().getMonthValue() == now.getMonthValue()
                        && d.getDate().getYear() == now.getYear())
                .count();
    }

    public long getTransactionsToday() {
        LocalDateTime now = LocalDateTime.now();
        return recentPayments.stream()
                .filter(d -> d.getDate().toLocalDate().equals(now.toLocalDate()))
                .count();
    }

    public long getUniqueDonors() {
        return recentPayments.stream().map(Donor::getUsername).distinct().count();
    }
}
