package com.downn_falls.events.commands.balance;

import com.downn_falls.PaymentBot;
import com.downn_falls.events.commands.CommandTreeNode;
import com.downn_falls.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.OffsetDateTime;

public class AddCommandTreeNode extends CommandTreeNode {
    public AddCommandTreeNode(CommandTreeNode parent) {
        super(parent, "add");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {

        event.deferReply().queue();

        if (event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {

            User user = event.getOption("user").getAsUser();
            PaymentBot.databaseManager.load(event.getGuild().getId(), user.getId(), (balanceData) -> {
                double amount = event.getOption("amount").getAsDouble();

                balanceData.setGuildName(event.getGuild().getName()).setUsername(user.getName());
                balanceData.add(amount);

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setTitle("เพิ่มยอดเงินสำเร็จ")
                        .setDescription("\u200e")
                        .addField("จำนวน", amount+" บาท", false)
                        .addField("คงเหลือ", balanceData.getBalance()+" บาท\n\u200e", false)
                        .setAuthor(user.getName(), null, user.getAvatarUrl())
                        .setFooter(event.getUser().getName(), event.getUser().getAvatarUrl())
                        .setTimestamp(OffsetDateTime.now())
                        .setColor(0x00ff96)
                        .build()
                ).queue();

            });

        } else {
            event.getHook().editOriginalEmbeds(Utils.errorEmbed("Mission Permission: MODERATE_MEMBERS")).queue();
        }
    }
}
