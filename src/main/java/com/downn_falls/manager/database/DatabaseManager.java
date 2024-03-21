package com.downn_falls.manager.database;

import com.downn_falls.PaymentBot;
import com.downn_falls.manager.BalanceData;
import com.downn_falls.manager.YamlManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.function.Consumer;

public class DatabaseManager {

    private final String HOST = YamlManager.getConfig("mysql.host", String.class);
    private final String USER = YamlManager.getConfig("mysql.user", String.class);
    private final String PASSWORD = YamlManager.getConfig("mysql.pass", String.class);
    private final String PORT = YamlManager.getConfig("mysql.port", String.class);
    private final String DATABASE = YamlManager.getConfig("mysql.database", String.class);
    private Connection connection;

    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.HOST + ":" + this.PORT + "/" + this.DATABASE + "?autoReconnect=true&useSSL=" +
                    YamlManager.getConfig("mysql.useSSL", String.class), this.USER, this.PASSWORD);

        } catch (SQLException e) {
            System.err.println("FAILED TO CONNECT TO MYSQL: "+e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public Connection getConnection() {
        try {
            if (this.connection.isClosed()) {
                System.out.println("Reconnecting to Database...");
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public void createTableIfNotExist() {

        try {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS payment_bot_test (ID int NOT NULL AUTO_INCREMENT, GUILD_ID VARCHAR(40), USER_ID VARCHAR(40), BALANCE DOUBLE(20, 4), GUILD_NAME VARCHAR(40), USERNAME VARCHAR(40), PRIMARY KEY (ID));");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS payment_bot_live (ID int NOT NULL AUTO_INCREMENT, GUILD_ID VARCHAR(40), USER_ID VARCHAR(40), BALANCE DOUBLE(20, 4), GUILD_NAME VARCHAR(40), USERNAME VARCHAR(40), PRIMARY KEY (ID));");
            statement.close();

        } catch (SQLException | NullPointerException e) {
            System.err.println("FAILED TO CREATE MYSQL TABLE");
            Thread.currentThread().interrupt();
        }
    }

    public void load(String guildId, String userId, Consumer<BalanceData> consumer) {

        try {

            Guild guild = PaymentBot.jda.getGuildById(guildId);
            String guildName = guild == null ? "" : guild.getName();

            User user = PaymentBot.jda.getUserById(userId);
            String userName = user == null ? "" : user.getName();

            double balance = 0;

            String table = PaymentBot.testMode ? "payment_bot_test" : "payment_bot_live";

            PreparedStatement addDataStatement = getConnection().prepareStatement(
                    "INSERT INTO " + table + " (GUILD_ID, USER_ID, BALANCE, GUILD_NAME, USERNAME)" +
                            "    SELECT ?, ?, ?, ?, ?" +
                            "    WHERE NOT EXISTS (SELECT * FROM " + table + " WHERE GUILD_ID = ? AND USER_ID = ?);"
            );

            addDataStatement.setString(1, guildId);
            addDataStatement.setString(2, userId);
            addDataStatement.setDouble(3, 0);
            addDataStatement.setString(4, guildName);
            addDataStatement.setString(5, userName);
            addDataStatement.setString(6, guildId);
            addDataStatement.setString(7, userId);
            addDataStatement.executeUpdate();
            addDataStatement.close();

            PreparedStatement selectStatement = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE GUILD_ID = ? AND USER_ID = ?");

            selectStatement.setString(1, guildId);
            selectStatement.setString(2, userId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("BALANCE");
            }
            selectStatement.close();

            consumer.accept(new BalanceData(guildId, userId, balance));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setBalance(String guildId, String userId, double value, String guildName, String username) {

        String table = PaymentBot.testMode ? "payment_bot_test" : "payment_bot_live";

        try (PreparedStatement statement = getConnection().prepareStatement("UPDATE "+table+" SET BALANCE = ?, GUILD_NAME = ?, USERNAME = ? WHERE GUILD_ID = ? AND USER_ID = ?;")) {

            statement.setDouble(1, value);
            statement.setString(2, guildName);
            statement.setString(3, username);
            statement.setString(4, guildId);
            statement.setString(5, userId);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

}
