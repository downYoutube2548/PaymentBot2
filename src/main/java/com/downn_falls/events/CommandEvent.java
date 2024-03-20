package com.downn_falls.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.downn_falls.events.commands.BalanceCommand;
import com.downn_falls.events.commands.TestModeCommand;
import com.downn_falls.events.commands.TopUpCommand;

public class CommandEvent extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("topup")) {
            new TopUpCommand().run(event);
        } else if (event.getName().equals("testmode")) {
            new TestModeCommand().run(event);
        } else if (event.getName().equals("balance")) {
            new BalanceCommand().run(event);
        }
    }
}
