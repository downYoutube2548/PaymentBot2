package com.downn_falls.events.commands.balance;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.downn_falls.events.commands.SubCommand;

public class CheckCommand implements SubCommand {
    @Override
    public void run(SlashCommandInteractionEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

        }
    }
}
