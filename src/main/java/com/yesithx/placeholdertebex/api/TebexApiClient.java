package com.yesithx.placeholdertebex.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yesithx.placeholdertebex.PlaceholderTebex;
import com.yesithx.placeholdertebex.model.Donor;
import com.yesithx.placeholdertebex.model.StoreInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TebexApiClient {

    private static final DateTimeFormatter TEBEX_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final PlaceholderTebex plugin;

    public TebexApiClient(PlaceholderTebex plugin) {
        this.plugin = plugin;
    }

    private String getSecretKey() {
        return plugin.getConfigManager().getSecretKey();
    }

    private String getBaseUrl() {
        return plugin.getConfigManager().getBaseUrl();
    }

    private String fetch(String endpoint) throws Exception {
        URI uri = URI.create(getBaseUrl() + endpoint);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Tebex-Secret", getSecretKey());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Tebex API returned status: " + status);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public List<Donor> fetchPayments(int limit) {
        List<Donor> donors = new ArrayList<>();
        try {
            String json = fetch("/payments?limit=" + limit);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray data = root.getAsJsonArray("data");

            for (JsonElement el : data) {
                JsonObject payment = el.getAsJsonObject();
                String username = getStringSafe(payment, "player", "username");
                double amount = payment.has("amount") ? payment.get("amount").getAsDouble() : 0.0;
                String currency = getNestedStringSafe(payment, "currency", "iso_4217");
                String dateStr = getStringSafe(payment, "date");
                LocalDateTime date = parseDateSafe(dateStr);
                String packageName = getFirstPackageName(payment);
                String transactionId = getStringSafe(payment, "transaction_id");
                String email = getStringSafe(payment, "email");

                donors.add(new Donor(username, amount, currency, date, packageName, transactionId, email));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[PlaceholderTebex] Failed to fetch payments: " + e.getMessage());
        }
        return donors;
    }

    public StoreInfo fetchStoreInfo() {
        try {
            String json = fetch("/information");
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject account = root.getAsJsonObject("account");
            JsonObject server = root.getAsJsonObject("server");

            String storeName = server != null ? getStringSafe(server, "name") : getStringSafe(account, "name");
            String currency = getNestedStringSafe(account, "currency", "iso_4217");
            String domain = getStringSafe(account, "domain");
            String description = getStringSafe(account, "name");
            int totalPackages = 0;

            return new StoreInfo(storeName, currency, domain, description, totalPackages);
        } catch (Exception e) {
            plugin.getLogger().warning("[PlaceholderTebex] Failed to fetch store info: " + e.getMessage());
            return null;
        }
    }

    private String getStringSafe(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return plugin.getConfigManager().getNoDataPlaceholder();
    }

    private String getStringSafe(JsonObject obj, String nestedKey, String key) {
        if (obj != null && obj.has(nestedKey) && obj.get(nestedKey).isJsonObject()) {
            JsonObject nested = obj.getAsJsonObject(nestedKey);
            return getStringSafe(nested, key);
        }
        return plugin.getConfigManager().getNoDataPlaceholder();
    }

    private String getNestedStringSafe(JsonObject obj, String nestedKey, String key) {
        return getStringSafe(obj, nestedKey, key);
    }

    private String getFirstPackageName(JsonObject payment) {
        if (payment.has("packages") && payment.get("packages").isJsonArray()) {
            JsonArray packages = payment.getAsJsonArray("packages");
            if (!packages.isEmpty()) {
                JsonObject pkg = packages.get(0).getAsJsonObject();
                return getStringSafe(pkg, "name");
            }
        }
        return plugin.getConfigManager().getNoDataPlaceholder();
    }

    private LocalDateTime parseDateSafe(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, TEBEX_DATE_FORMAT);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
