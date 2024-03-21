package com.downn_falls.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Utils {
    public static MessageEmbed errorEmbed(String text) {
        return new EmbedBuilder().setColor(0xff0000).setTitle("Error!: "+text).build();
    }
}
