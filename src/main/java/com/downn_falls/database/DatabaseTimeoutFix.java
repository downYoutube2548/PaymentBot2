package com.downn_falls.database;

import com.downn_falls.PaymentBot;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseTimeoutFix {
    public static void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the task to run every 5 minutes, starting from now
        scheduler.scheduleAtFixedRate(()-> {
            try {
                Statement statement = PaymentBot.databaseManager.getConnection().createStatement();
                statement.executeQuery("SELECT * FROM payment_bot_test;");
            } catch (SQLException ignored) {
            }
        }, 0, 5, TimeUnit.MINUTES);
    }
}
