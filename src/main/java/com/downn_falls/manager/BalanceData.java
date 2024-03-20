package com.downn_falls.manager;

import com.downn_falls.PaymentBot;

public class BalanceData {

    private final String guildId;
    private final String userId;
    private double balance;

    private String guildName;
    private String userName;

    public BalanceData(String guildId, String userId, double balance) {
        this.guildId = guildId;
        this.userId = userId;
        this.balance = balance;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getUserId() {
        return userId;
    }

    public double getBalance() {
        return this.balance;
    }

    public void set(double value) {
        this.balance = Math.max(0, value);
        PaymentBot.databaseManager.setBalance(guildId, userId, balance, guildName, userName);
    }

    public void add(double value) {
        this.balance += Math.max(0, value);

        PaymentBot.databaseManager.setBalance(guildId, userId, balance, guildName, userName);
    }

    public void remove(double value) {
        this.balance -= value >= this.balance ? 0 : Math.max(0, value);
        PaymentBot.databaseManager.setBalance(guildId, userId, balance, guildName, userName);
    }

    public BalanceData setGuildName(String guildName) {
        this.guildName = guildName;
        return this;
    }

    public BalanceData setUsername(String userName) {
        this.userName = userName;
        return this;
    }
}
