package de.Breakcraft.Bot.Commands;

import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerInfo extends Command {

    public ServerInfo() {
        name = "ServerInfo";
    }

    @Override
    public void runCommand(Message msg, Member member) {
        try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from ServerInfo WHERE id = 1")) {
            ResultSet set = preparedStatement.executeQuery();
            set.next();
            int player = Integer.parseInt(set.getString("value"));
            set.close();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Server Info", msg.getGuild().getIconUrl(), msg.getJDA().getSelfUser().getAvatarUrl());
            builder.setColor(Color.BLUE);
            builder.setThumbnail(msg.getGuild().getIconUrl());
            builder.addField("Aktuelle Spieleranzahl:", "`" + player + "`", true);
            builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
            msg.getChannel().sendMessageEmbeds(builder.build()).queue();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
