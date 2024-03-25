package com.downn_falls.events;

import com.downn_falls.PaymentBot;
import com.downn_falls.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandEvent extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.isFromGuild()) {

            if (PaymentBot.commandMap.containsKey(event.getName())) {
                PaymentBot.commandMap.get(event.getName()).run(event);
            }

        } else {
            event.getInteraction().replyEmbeds(Utils.errorEmbed("You must be in the Guild!")).setEphemeral(true).queue();
        }
    }
}
