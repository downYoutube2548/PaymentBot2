package com.downn_falls.events.commands;

import com.stripe.Stripe;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import com.downn_falls.PaymentBot;
import com.downn_falls.manager.YamlManager;

import java.util.ArrayList;

public class TestModeCommand implements SubCommand{
    @Override
    public void run(SlashCommandInteractionEvent event) {
        boolean toggle = event.getOption("toggle").getAsBoolean();

        MessageEmbed embed = new MessageEmbed(
                null,
                "Test Mode: "+toggle,
                null,
                EmbedType.UNKNOWN,
                null,
                16760463,
                null,
                null,
                null,
                null,
                null,
                null,
                new ArrayList<>()
        );

        event.getInteraction().reply(MessageCreateData.fromEmbeds(embed)).queue();

        PaymentBot.testMode = toggle;
        Stripe.apiKey = toggle ? YamlManager.getConfig("stripe-test-key", String.class) : YamlManager.getConfig("stripe-live-key", String.class);

    }
}
