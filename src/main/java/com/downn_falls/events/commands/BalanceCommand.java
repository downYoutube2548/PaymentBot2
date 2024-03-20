package com.downn_falls.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.downn_falls.events.commands.balance.AddCommand;
import com.downn_falls.events.commands.balance.CheckCommand;
import com.downn_falls.events.commands.balance.RemoveCommand;
import com.downn_falls.events.commands.balance.SetCommand;

public class BalanceCommand implements SubCommand {
    @Override
    public void run(SlashCommandInteractionEvent event) {
        if (event.getSubcommandName().equals("check")) {
            new CheckCommand().run(event);
        } else if (event.getSubcommandName().equals("add")) {
            new AddCommand().run(event);
        } else if (event.getSubcommandName().equals("remove")) {
            new RemoveCommand().run(event);
        } else if (event.getSubcommandName().equals("set")) {
            new SetCommand().run(event);
        }
    }
}
