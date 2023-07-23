package de.Breakcraft.Bot.Commands;

import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

public class Level extends Command {

    public Level() {
        name = "level";
    }

    @Override
    public void runCommand(Message msg, Member author) {
        if(!(BotMain.instance.data.levels.containsKey(author.getIdLong()))) {
            BotMain.instance.data.levels.put(author.getIdLong(), 0);
            BotMain.instance.data.messages.put(author.getIdLong(), 0);
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Level Info", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
        builder.setDescription("Level Info von " + author.getAsMention());
        builder.setColor(Color.BLUE);
        builder.addField("Aktuelles Level:" , "`" + BotMain.instance.data.levels.get(author.getIdLong()) + "`", true);
        int nextLevel;
        if(BotMain.instance.data.levels.get(author.getIdLong()) != 0) {
            nextLevel = 10 * (5 * BotMain.instance.data.levels.get(author.getIdLong()));
        } else nextLevel = 10;
        builder.addField("Aktueller Stand:", "`" + BotMain.instance.data.messages.get(author.getIdLong()) + "` / `" + nextLevel + "`", true);
        builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
        msg.getTextChannel().sendMessage(builder.build()).queue();
    }

}
