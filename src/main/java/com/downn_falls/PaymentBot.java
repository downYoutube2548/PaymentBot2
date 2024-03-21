package com.downn_falls;

import com.downn_falls.events.CommandEvent;
import com.downn_falls.manager.YamlManager;
import com.downn_falls.manager.database.DatabaseManager;
import com.downn_falls.manager.database.DatabaseTimeoutFix;
import com.downn_falls.webhook.WebServer;
import com.stripe.Stripe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

public class PaymentBot {

    public static JDA jda;

    public static HashMap<UUID, SlashCommandInteractionEvent> sessionMap = new HashMap<>();
    public static boolean testMode = true;

    public static DatabaseManager databaseManager;

    public static void main(String[] args) {

        YamlManager.saveDefaultConfig();

        int port = YamlManager.getConfig("web-server.port", Integer.class);

        try {
            // Try to create a socket on the specified port
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();

        } catch (IOException e) {
            System.err.println("FAILED TO BIND TO PORT "+port+": "+e.getMessage());
            Thread.currentThread().interrupt();
        }

        databaseManager = new DatabaseManager();
        databaseManager.connect();
        databaseManager.createTableIfNotExist();

        DatabaseTimeoutFix.start();

        Stripe.apiKey = testMode ? YamlManager.getConfig("stripe-test-key", String.class) : YamlManager.getConfig("stripe-live-key", String.class);

        jda = JDABuilder.create(YamlManager.getConfig("discord-bot-token", String.class), GatewayIntent.GUILD_MESSAGES)
                .build();

        jda.addEventListener(new CommandEvent());

        jda.updateCommands().addCommands(
                Commands.slash("topup", "Top Up")
                        .addOption(OptionType.NUMBER, "price", "price", true, true),
                Commands.slash("testmode", "switch to test mode")
                        .addOption(OptionType.BOOLEAN, "toggle", "toggle", true),
                Commands.slash("balance", "Check your balance")
                        .addSubcommands(
                                new SubcommandData("check", "Check user's balance").addOption(OptionType.USER, "user", "User to check balance", false),
                                new SubcommandData("add", "Add user's balance")
                                        .addOption(OptionType.USER, "user", "User to add balance", true)
                                        .addOption(OptionType.NUMBER, "amount", "Amount of balance", true),
                                new SubcommandData("set", "Set user's balance")
                                        .addOption(OptionType.USER, "user", "User to set balance", true)
                                        .addOption(OptionType.NUMBER, "amount", "Amount of balance", true),
                                new SubcommandData("remove", "Remove user's balance")
                                        .addOption(OptionType.USER, "user", "User to remove balance", true)
                                        .addOption(OptionType.NUMBER, "amount", "Amount of balance", true)
                        )

        ).queue();

        WebServer.start();

    }
}