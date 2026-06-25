package com.yesithx.placeholdertebex.model;

public class StoreInfo {

    private final String storeName;
    private final String currency;
    private final String domain;
    private final String description;
    private final int totalPackages;

    public StoreInfo(String storeName, String currency, String domain,
                     String description, int totalPackages) {
        this.storeName = storeName;
        this.currency = currency;
        this.domain = domain;
        this.description = description;
        this.totalPackages = totalPackages;
    }

    public String getStoreName() { return storeName; }
    public String getCurrency() { return currency; }
    public String getDomain() { return domain; }
    public String getDescription() { return description; }
    public int getTotalPackages() { return totalPackages; }
}
