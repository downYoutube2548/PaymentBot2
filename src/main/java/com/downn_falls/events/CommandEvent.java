package com.downn_falls.events;

import com.downn_falls.events.commands.BalanceCommand;
import com.downn_falls.events.commands.TestModeCommand;
import com.downn_falls.events.commands.TopUpCommand;
import com.downn_falls.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandEvent extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.isFromGuild()) {

            if (event.getName().equals("topup")) {
                new TopUpCommand().run(event);
            } else if (event.getName().equals("testmode")) {
                new TestModeCommand().run(event);
            } else if (event.getName().equals("balance")) {
                new BalanceCommand().run(event);
            }

        } else {
            event.getInteraction().replyEmbeds(Utils.errorEmbed("You must be in the Guild!")).setEphemeral(true).queue();
        }
    }
}
