package com.downn_falls.events.commands;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import com.downn_falls.PaymentBot;

import java.time.OffsetDateTime;
import java.util.List;

public class TopUpCommand implements SubCommand {

    @Override
    public void run(SlashCommandInteractionEvent event) {
        MessageEmbed embed = new MessageEmbed(
                null,
                "เลือกช่องทางการชำระเงิน",
                "\u200e",
                EmbedType.UNKNOWN,
                OffsetDateTime.now(),
                16760463,
                null,
                null,
                null,
                null,
                PaymentBot.testMode ? new MessageEmbed.Footer("*test mode enabled", null, null) : null,
                null,
                List.of(
                        new MessageEmbed.Field("ราคา (Price):", event.getOption("price").getAsDouble()+" บาท\n\u200e", false)
                )
        );

        event.getInteraction().reply(MessageCreateData.fromEmbeds(embed))
                .addActionRow(StringSelectMenu.create("payment_method;"+Math.max(10, event.getOption("price").getAsDouble()))
                        .setPlaceholder("กรุณาเลือกช่องทางการชำระเงิน")
                        .addOption("พร้อมเพย์ (PromptPay)", "promptpay", Emoji.fromCustom("promptpay", 1217336040998567947L, false))
                        .build())
                .setEphemeral(true)
                .queue();
    }
}
