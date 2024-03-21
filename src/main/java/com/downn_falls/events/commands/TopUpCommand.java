package com.downn_falls.events.commands;

import com.downn_falls.PaymentBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.time.OffsetDateTime;

public class TopUpCommand implements SubCommand {

    @Override
    public void run(SlashCommandInteractionEvent event) {

        event.getInteraction().replyEmbeds(new EmbedBuilder()
                        .setTitle("เลือกช่องทางการชำระเงิน")
                        .setDescription("\u200e")
                        .setColor(16760463)
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter(PaymentBot.testMode ? "Test mode enabled" : null, null)
                        .addField("ราคา (Price):", event.getOption("price").getAsDouble()+" บาท\n\u200e", false)
                        .build()
                )
                .addActionRow(StringSelectMenu.create("payment_method;"+Math.max(10, event.getOption("price").getAsDouble()))
                        .setPlaceholder("กรุณาเลือกช่องทางการชำระเงิน")
                        .addOption("พร้อมเพย์ (PromptPay)", "promptpay", Emoji.fromCustom("promptpay", 1217336040998567947L, false))
                        .build())
                .setEphemeral(true)
                .queue();
    }
}
