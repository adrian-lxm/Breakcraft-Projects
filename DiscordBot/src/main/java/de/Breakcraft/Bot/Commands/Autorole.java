package de.Breakcraft.Bot.Commands;

import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;

public class Autorole extends Command {

    public Autorole() {
        name = "autorole";
    }

    @Override
    public void runCommand(Message msg, Member member) {
        if(member.hasPermission(Permission.MANAGE_ROLES)) {
            if(msg.getContentRaw().split(" ").length == 2 && msg.getMentionedRoles().size() > 0) {
                Role toAdd = msg.getMentionedRoles().get(0);
                BotMain.instance.data.autoroles.add(toAdd.getIdLong());
                msg.getChannel().sendMessage(":white_check_mark: Die Role " + toAdd.getAsMention() + " wurde zur den Autoroles hinzugefügt !").queue();
            } else {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.BLUE);
                embedBuilder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                embedBuilder.setAuthor("Autorole Command Usage", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
                embedBuilder.setDescription("**Usage:**\n`bc!autorole @Role`");
                embedBuilder.addField("Example:", "`bc!autorole @Spieler`", false);
                msg.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Dazu hast du nicht die Rechte !");
            builder.setDescription("Tja da hast du leider Pech gehabt.\nLeider hast du für diese Aktion nicht die Rechte !");
            builder.setColor(Color.RED);
            builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
            msg.getChannel().sendMessage(builder.build()).queue();
        }
    }

}
