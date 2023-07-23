package de.Breakcraft.Bot.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class News extends Command {

    public News() {
        name = "news";
    }

    @Override
    public void runCommand(Message msg, Member author) {
        if(author.hasPermission(Permission.MANAGE_SERVER)) {
            String[] args = msg.getContentRaw().split(" ");
            if(args.length == 1) {
                helpMsg(msg);
                return;
            }
            if(args.length == 2) {
                helpMsg(msg);
                return;
            }
            if(args.length > 2) {
                try {
                    int id = Integer.parseInt(args[1]);
                    NewsType type = NewsType.getTypeById(id);
                    if(type != null) {
                        TextChannel news = msg.getGuild().getTextChannelById("804827901081681971");
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(type.getColor());
                        builder.setTitle("**" + type.getTypeText() + "**");
                        StringBuilder builder2 = new StringBuilder();
                        for(int i = 2; i < args.length; i++) builder2.append(args[i] + " ");
                        builder.setDescription(builder2.toString());
                        builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                        news.sendMessage(msg.getGuild().getPublicRole().getAsMention()).embed(builder.build()).queue((success) -> {
                            msg.getTextChannel().sendMessage("\u2705 Neuigkeiten wurde veröffentlicht !").queue();
                        });
                    } else msg.getTextChannel().sendMessage(":x: Das ist keine valide ID !\n   Benutze `bc!news` um alle IDs zu sehen.").queue();
                } catch (NumberFormatException e) {
                    msg.getTextChannel().sendMessage(":x: Das ist keine valide Nummer !").queue();
                }
            }
        } else msg.getTextChannel().sendMessage(":x: Dazu hast du nicht die Rechte !").queue();
    }

    private void helpMsg(Message msg) {
        EmbedBuilder embedBuilder= new EmbedBuilder();
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.setTitle("bc!news Command");
        embedBuilder.setDescription("**Usage:**\n`bc!news [type-id] [message]`");
        StringBuilder builder = new StringBuilder();
        for(NewsType type : NewsType.values()) {
            if(type.getID() != 5) {
                builder.append(type.getID() + " -> " + type.getTypeText() + "\n");
            } else {
                builder.append(type.getID() + " -> " + type.getTypeText());
            }
        }
        embedBuilder.addField("ID's :", builder.toString(), false);
        embedBuilder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
        msg.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private enum NewsType {

        INFO(1, "Info zum Server", Color.BLUE),
        LITTLE_INFO(2, "Kleine Info zum Server", Color.CYAN),
        IMPORTANT_INFO(3, "Wichtige Info zum Server", Color.GREEN),
        TEAM_INFO(4, "Info zum Serverteam", Color.getHSBColor(257, 53, 46)),
        TEAM_UPDATE(5, "Aktualisierungen im Serverteam", Color.getHSBColor(257, 53, 46));

        private final int id;
        private final String typeText;
        private final Color color;
        private NewsType(int id, String typeText, Color color) {
            this.id = id;
            this.typeText = typeText;
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public int getID() {
            return id;
        }

        public String getTypeText() {
            return typeText;
        }

        public static NewsType getTypeById(int id) {
            for(NewsType type : NewsType.values()) {
                if(type.id == id) return type;
            }
            return null;
        }

    }

}
