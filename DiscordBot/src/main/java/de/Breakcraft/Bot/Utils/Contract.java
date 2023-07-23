package de.Breakcraft.Bot.Utils;

import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Contract {
    public int sqlId;
    public String title;
    public List<MessageEmbed.Field> fields;
    public long role;
    public long channel;
    public long claimed = -1;
    public long message = -1;

    public Contract(int sqlId, String title, List<MessageEmbed.Field> fields, long role, long channel)  {
        this.sqlId = sqlId;
        this.title = title;
        this.fields = fields;
        this.role = role;
        this.channel = channel;
    }

    public void send() {
        JDA jda = BotMain.instance.jda;
        TextChannel channel = jda.getGuildById("804789138850840706").getTextChannelById(this.channel);
        Role role = jda.getGuildById("804789138850840706").getRoleById(this.role);
        channel.sendMessage(role.getAsMention()).embed(buildContract(true)).queue((message) -> {
            message.addReaction("\u2705").queue((success) -> {
                this.message = message.getIdLong();
                try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update Contracts set sended = ? where id = ?")) {
                    preparedStatement.setBoolean(1, true);
                    preparedStatement.setInt(2, sqlId);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void handleReactionEvent(MessageReactionAddEvent e) {
        JDA jda = BotMain.instance.jda;
        TextChannel abgeschlossen = jda.getGuilds().get(0).getTextChannelById("820383107982295040");
        StringBuilder builder = new StringBuilder();
        builder.append("**Auftrag `" + title + "` abgeschlossen von** " + e.getUser().getAsMention() + "\n\n");
        StringBuilder attachments = new StringBuilder();
        for(Message message : e.getChannel().getHistoryAfter(this.message, 20).complete().getRetrievedHistory()) {
            if(!(message.getContentRaw().isEmpty())) {
                builder.append(message.getContentRaw() + "\n");
            }
            for(Message.Attachment attachment : message.getAttachments()) {
                attachments.append(attachment.getUrl() + "\n");
            }
        }
        builder.append("\n**Attachments:**\n");
        builder.append(attachments.toString());
        abgeschlossen.sendMessage(builder.toString()).queue((success2) -> {
            e.getChannel().sendMessage("\u2705 Der Auftrag wurde abgeschlossen und Ergebnisse wurden abgeschickt !").queue();
            BotMain.contracts.remove(this);
            try {
                String sql = "update Contracts set completed = ? where id = ?";
                PreparedStatement preparedStatement = BotMain.instance.dataSource.prepareStatement(sql);
                preparedStatement.setBoolean(1, true);
                preparedStatement.setInt(2, sqlId);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        });
    }

    public void handleGuildReactionEvent(GuildMessageReactionAddEvent e) {
        e.getReaction().clearReactions().queue((success) -> {
            Role role = e.getGuild().getRoleById(this.role);
            if(e.getMember().getRoles().contains(role)) {
                e.getMember().getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessageEmbeds(buildContract(false)).queue((message) -> {
                        message.addReaction("\u2705").queue((success2) -> {
                            e.getChannel().editMessageById(this.message, "\u2705 Auftrag wurde geclaimed von " + e.getMember().getAsMention() +" !").queue((message2) -> {
                                message2.suppressEmbeds(true).queue((success3) -> {
                                    claimed = e.getMember().getIdLong();
                                    this.message = message.getIdLong();
                                    try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update Contracts set claimed = ? where id = ?")) {
                                        preparedStatement.setLong(1, e.getMember().getIdLong());
                                        preparedStatement.setInt(2, sqlId);
                                        preparedStatement.executeUpdate();
                                    } catch (SQLException e2) {
                                        e2.printStackTrace();
                                    }
                                });
                            });
                        });
                    });
                });
            } else e.getMember().getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(":x: Du gehörst nicht dieser Auftragsgruppe an !").queue();
            });
        });
    }

    public MessageEmbed buildContract(boolean withReactforClaim) {
        JDA jda = BotMain.instance.jda;
        Role role = jda.getGuilds().get(0).getRoleById(this.role);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(role.getColor());
        builder.setTitle(title);
        for(MessageEmbed.Field field : fields) {
            builder.addField(field);
        }
        if(withReactforClaim) {
            builder.addBlankField(false);
            builder.addField("Kleiner Tipp:", "Um den Auftrag zu claimen, reagiere mit \u2705 !", false);
        } else {
            builder.addBlankField(false);
            builder.addField("Kleine Info:", "Um den Auftrag abzuschließen, reagiere mit \u2705 !\nSämtliche Nachrichten und Dateien, die du hier reinschickst werden weitergeleitet (Max. 20 Nachrichten/Dateien möglich).", false);
        }
        builder.setFooter("Breakcraft Bot by Shiru", jda.getSelfUser().getAvatarUrl());
        return builder.build();
    }

}
