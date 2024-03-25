package com.downn_falls.events.commands;

import com.downn_falls.PaymentBot;
import com.downn_falls.manager.YamlManager;
import com.stripe.Stripe;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TestModeCommandTreeNode extends CommandTreeNode {
    public TestModeCommandTreeNode(CommandTreeNode parent) {
        super(parent, "testmode");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        boolean toggle = event.getOption("toggle").getAsBoolean();

        event.getInteraction().replyEmbeds(new EmbedBuilder().setTitle("Test Mode: "+toggle).setColor(16760463).build()).queue();

        PaymentBot.testMode = toggle;
        Stripe.apiKey = toggle ? YamlManager.getConfig("stripe-test-key", String.class) : YamlManager.getConfig("stripe-live-key", String.class);

    }
}
