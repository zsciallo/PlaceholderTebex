package com.yesithx.placeholdertebex.model;

import java.time.LocalDateTime;

public class Donor {

    private final String username;
    private final double amount;
    private final String currency;
    private final LocalDateTime date;
    private final String packageName;
    private final String transactionId;
    private final String email;

    public Donor(String username, double amount, String currency, LocalDateTime date,
                 String packageName, String transactionId, String email) {
        this.username = username;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.packageName = packageName;
        this.transactionId = transactionId;
        this.email = email;
    }

    public String getUsername() { return username; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public LocalDateTime getDate() { return date; }
    public String getPackageName() { return packageName; }
    public String getTransactionId() { return transactionId; }
    public String getEmail() { return email; }
}
