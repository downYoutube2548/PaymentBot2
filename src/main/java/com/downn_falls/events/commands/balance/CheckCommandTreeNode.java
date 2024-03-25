package com.downn_falls.events.commands.balance;

import com.downn_falls.PaymentBot;
import com.downn_falls.events.commands.CommandTreeNode;
import com.downn_falls.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CheckCommandTreeNode extends CommandTreeNode {
    public CheckCommandTreeNode(CommandTreeNode parent) {
        super(parent, "check");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        if (event.getOption("user") != null) {
            if (event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
                User user = event.getOption("user").getAsUser();
                PaymentBot.databaseManager.load(event.getGuild().getId(), user.getId(), (balanceData) -> {
                    double amount = balanceData.getBalance();

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setTitle("จำนวนเงินทั้งหมด")
                            .setDescription(amount+" บาท")
                            .setColor(0x8b8eff)
                            .setAuthor(user.getName(), null, user.getAvatarUrl())
                            .build()
                    ).queue();
                });
            } else {
                event.getHook().editOriginalEmbeds(Utils.errorEmbed("Mission Permission: MODERATE_MEMBERS")).queue();
            }
        } else {
            PaymentBot.databaseManager.load(event.getGuild().getId(), event.getUser().getId(), (balanceData) -> {
                double amount = balanceData.getBalance();

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setTitle("จำนวนเงินของคุณ")
                        .setDescription(amount+" บาท")
                        .setColor(0x8b8eff)
                        .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                        .build()
                ).queue();
            });
        }
    }
}
