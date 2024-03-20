package com.downn_falls.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SubCommand {
    void run(SlashCommandInteractionEvent event);
}
