package de.Breakcraft.Bot.Commands;

import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

public class Support extends Command {

    public Support() {
        name = "support";
    }

    @Override
    public void runCommand(Message msg, Member author) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Support", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
        builder.setDescription("Reagiere mit ✅ um einen Support Channel auf zu machen !");
        builder.setColor(Color.BLUE);
        builder.setFooter("Breakcraft-Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
        msg.getChannel().sendMessage(builder.build()).queue((message) -> {
            message.addReaction("✅").queue((success) -> {
                BotMain.supportMsg = message.getIdLong();
            });
        });
    }

}
