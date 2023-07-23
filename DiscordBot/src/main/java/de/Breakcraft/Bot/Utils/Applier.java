package de.Breakcraft.Bot.Utils;

import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Applier {
    public static List<String> questions = Arrays.asList("Als was willst du dich bewerben ?", "Wer bist du ? (Erzähl was über dich)", "Warum Breakcraft ?", "Wieso du in dieser Rolle ?", "Wie sehen deine Online Zeiten aus ?", "Sonstiges ?");
    public Member member;
    public int sqlId;
    public long textChannel;
    public HashMap<Integer, MessageEmbed.Field> fields;

    public Applier(Member member, int sqlId, long textChannel) {
        this.member = member;
        this.textChannel = textChannel;
        this.sqlId = sqlId;
        this.fields = new HashMap<>();
        for(String s : questions) {
            int i = (questions.indexOf(s) + 1);
            fields.put(i, null);
        }
        uploadEntry();
    }

    public boolean answeredQuestion(int i) {
        return fields.containsKey(i);
    }

    public TextChannel getTextChannel() {
        JDA thisjda = BotMain.instance.jda;
        return thisjda.getGuilds().get(0).getTextChannelById(textChannel);
    }

    private void uploadEntry() {
        try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("insert into Applies values (?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, sqlId);
            preparedStatement.setDouble(2, member.getIdLong());
            preparedStatement.setString(3,member.getUser().getAsTag());
            preparedStatement.setDouble(4, System.currentTimeMillis());
            preparedStatement.setString(5, "open");
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void sendStartMessage() {
        TextChannel applierChannel = getTextChannel();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Deine Bewerbung", member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
        builder.setColor(Color.BLUE);
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : questions) {
            int i = (questions.indexOf(s) + 1);
            stringBuilder.append(i + ". `" + s + "`\n");
        }
        stringBuilder.append("(Sonstiges kannst du auslassen)");
        builder.addField("Fragen, die du beantworten musst:", stringBuilder.toString(), false);
        builder.setFooter("Breakcraft Bot by Shiru", member.getJDA().getSelfUser().getAvatarUrl());

        EmbedBuilder builder2 = new EmbedBuilder();
        builder2.setAuthor("Commands in diesen Channel", member.getGuild().getIconUrl(), member.getGuild().getIconUrl());
        builder2.setColor(Color.BLUE);
        builder2.addField("bc!bewerben [id] [antwort]", "So speicherst du eine Antwort für deine Bewerbung\nMit id ist die Zahl vor der Frage gemeint.", false);
        builder2.addField("bc!bewerben vorschau", "Sehe eine Vorschau deiner Bewerbung, die genauso bei uns sein wird", false);
        builder2.addField("bc!bewerben send", "Sendet die Bewerbung an uns und der Channel wird gelöscht", false);
        builder2.addField("bc!bewerben abbrechen", "Bricht die Bewerbung ab und löscht den Channel", false);
        builder2.addBlankField(false);
        builder2.addField("Kleine Warnung !", "Spam nicht mit Bewerbung, denn diese können zurückverflogt werden !", false);
        builder2.setFooter("Breakcraft Bot by Shiru", member.getJDA().getSelfUser().getAvatarUrl());

        applierChannel.sendMessage(member.getAsMention()).queue((success) -> {
            applierChannel.sendMessage(builder.build()).queue((success2) -> {
                applierChannel.sendMessage(builder2.build()).queue();
            });
        });

    }

    public MessageEmbed buildApplication() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Bewerbung von " + member.getUser().getAsTag(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
        builder.setColor(Color.GREEN);
        for(int i : fields.keySet()) {
            if(fields.get(i) != null) {
                builder.addField(fields.get(i));
            } else {
                builder.addField(questions.get(i - 1), "Nicht angegeben", false);
            }
        }
        builder.setFooter("Breakcraft Bot by Shiru", member.getJDA().getSelfUser().getAvatarUrl());
        return builder.build();
    }

    public void setStatus(String status) {
        try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("update Applies set `status` = ? where `id` = ?")) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, sqlId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
