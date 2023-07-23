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
import java.util.UUID;

public class Verify extends Command {

    public Verify() {
        name = "verify";
    }

    @Override
    public void runCommand(Message msg, Member author) {
        String[] args = msg.getContentRaw().split(" ");
        String tag = author.getUser().getAsTag();
        if(args.length == 2) {
            try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Verifications` where `discord-tag` = ?")) {
                int pin = Integer.parseInt(args[1]);
                preparedStatement.setString(1, tag);
                ResultSet set = preparedStatement.executeQuery();
                if(set.next()) {
                    if(set.getInt("pin") == pin) {
                        UUID player = UUID.fromString(set.getString("uuid"));
                        set.close();
                        PreparedStatement preparedStatement2 = connection.prepareStatement("update Verifications set verified = ? where uuid = ?");
                        preparedStatement2.setBoolean(1, true);
                        preparedStatement2.setString(2, String.valueOf(player));
                        preparedStatement2.executeUpdate();
                        preparedStatement2.close();
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.GREEN);
                        builder.setAuthor("Verifizierung erfolgreich !", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
                        builder.setDescription("Rejoine und du kannst los legen ;)");
                        builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                        msg.getChannel().sendMessage(builder.build()).queue();
                    } else {
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.RED);
                        builder.setAuthor("Verifizierung fehlgeschlagen !", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
                        builder.setDescription("Die Verifikation ist aus folgendem Grund fehlgeschlagen :\nFalsche Pin");
                        builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                        msg.getChannel().sendMessage(builder.build()).queue();
                    }
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.RED);
                    builder.setAuthor("Verifizierung fehlgeschlagen !", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
                    builder.setDescription("Die Verifikation ist aus folgendem Grund fehlgeschlagen :\nKeine Verifikationsanfragen über diesem Account");
                    builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                    msg.getChannel().sendMessage(builder.build()).queue();
                }
            } catch (NumberFormatException e) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.RED);
                builder.setAuthor("Ungütlige Command Syntax", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
                builder.setDescription("**Usage:**\n`bc!verify [Pin]`");
                builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                msg.getChannel().sendMessage(builder.build()).queue();
            } catch (SQLException e2) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.RED);
                builder.setAuthor("Fehler bei der Datenbank Verbindung", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
                builder.setDescription("Versuche es später erneut !");
                builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                msg.getChannel().sendMessage(builder.build()).queue();
            }
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setAuthor("Ungütlige Command Syntax", author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
            builder.setDescription("**Usage:**\n`bc!verify [Pin]`");
            builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
            msg.getChannel().sendMessage(builder.build()).queue();
        }
    }

}
