package de.Breakcraft.Bot.Commands;

import de.Breakcraft.Bot.BotMain;
import de.Breakcraft.Bot.Utils.Applier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Bewerben extends Command {
    public List<Applier> appliers = new ArrayList<>();

    public Bewerben() {
        name = "bewerben";
    }

    @Override
    public void runCommand(Message msg, Member author) {
        try {
            String[] args = msg.getContentRaw().substring(12).split(" ");
            if(args.length == 1) {
                if(args[0].equals("start")) {
                    if(!(containsApplier(author.getIdLong()))) {
                        Category applies = msg.getGuild().getCategoryById("814587131052818442");
                        int id = 0;
                        try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Applies`")) {
                            ResultSet set = preparedStatement.executeQuery();
                            while(set.next()) id = set.getInt("id");
                            id++;
                            Role everyone = msg.getGuild().getPublicRole();
                            AtomicLong textChannelId = new AtomicLong();
                            applies.createTextChannel("applier-" + id)
                                    .addRolePermissionOverride(everyone.getIdLong(), null, Collections.singleton(Permission.VIEW_CHANNEL))
                                    .addMemberPermissionOverride(author.getIdLong(), Collections.singleton(Permission.VIEW_CHANNEL), null)
                                    .queue((textChannel) -> {
                                        textChannelId.set(textChannel.getIdLong());
                                    });
                            while (textChannelId.get() == 0);
                            Applier applier = new Applier(author, id, textChannelId.get());
                            appliers.add(applier);
                            applier.sendStartMessage();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                            msg.getTextChannel().sendMessage(":x: Fehler bei der SQL Verbindung").queue();
                        }
                    } else msg.getTextChannel().sendMessage(":x: Du hast eine aktuell laufende Bewerbung !").queue();
                } else {
                    Applier applier = null;
                    for(Applier applier2 : appliers) {
                        if(applier2.member.getIdLong() == author.getIdLong()) {
                            applier = applier2;
                            break;
                        }
                    }

                    if(applier != null) {
                        if(msg.getTextChannel().getIdLong() == applier.textChannel) {
                            if(args[0].equals("vorschau")) {
                                msg.getTextChannel().sendMessage(applier.buildApplication()).queue();
                            } else if(args[0].equals("send")) {
                                boolean allow = true;
                                for(int i : applier.fields.keySet()) {
                                    if(applier.fields.get(i) == null) {
                                        if(i != 6) { allow = false; break; }
                                    }
                                }
                                if(allow) {
                                    TextChannel bewerbungen = msg.getGuild().getTextChannelById("816035712934871041");
                                    appliers.remove(applier);
                                    bewerbungen.sendMessage(applier.buildApplication()).queue((success) -> {
                                        msg.getTextChannel().sendMessage(":white_check_mark: Deine Bewerbung wurde gesendet !\nDer Kanal wird in 5 Sekunden gelöscht").queue((success2) -> {
                                            msg.getTextChannel().delete().queueAfter(5, TimeUnit.SECONDS);
                                        });
                                    });
                                    applier.setStatus("sended");
                                } else {
                                    msg.getTextChannel().sendMessage(":x: Es sind noch nicht alle benötigten Fragen beantwortet !").queue();
                                }
                            } else if(args[0].equals("abbrechen")) {
                                appliers.remove(applier);
                                msg.getTextChannel().sendMessage(":white_check_mark: Deine Bewerbung wurde abgebrochen!\nChannel wird in 5 Sekunden gelöscht...").queue((success) -> {
                                    msg.getTextChannel().delete().queueAfter(5, TimeUnit.SECONDS);
                                });
                                applier.setStatus("aborted");
                            } else {
                                EmbedBuilder builder2 = new EmbedBuilder();
                                builder2.setAuthor("Commands in diesen Channel", author.getGuild().getIconUrl(), author.getGuild().getIconUrl());
                                builder2.setColor(Color.BLUE);
                                builder2.addField("bc!bewerbung [id] [antwort]", "So speicherst du eine Antwort für deine Bewerbung\nMit id ist die Zahl vor der Frage gemeint.", false);
                                builder2.addField("bc!bewerbung vorschau", "Sehe eine Vorschau deiner Bewerbung, die genauso bei uns sein wird", false);
                                builder2.addField("bc!bewerbung send", "Sendet die Bewerbung an uns und der Channel wird gelöscht", false);
                                builder2.addField("bc!bewerbung abbrechen", "Bricht die Bewerbung ab und löscht den Channel", false);
                                builder2.addBlankField(false);
                                builder2.addField("Kleine Warnung !", "Spam nicht mit Bewerbung, denn diese können zurückverflogt werden !", false);
                                builder2.setFooter("Breakcraft Bot by Shiru", author.getJDA().getSelfUser().getAvatarUrl());
                                msg.getTextChannel().sendMessage(builder2.build()).queue();
                            }
                        } else {
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setAuthor("Bewerbungscommand", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
                            builder.setColor(Color.blue);
                            builder.addField("Usage:", "`bc!bewerben start`\nStartet ein Bewerbungschat", false);
                            builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                            msg.getTextChannel().sendMessage(builder.build()).queue();
                        }
                    } else {
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setAuthor("Bewerbungscommand", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
                        builder.setColor(Color.blue);
                        builder.addField("Usage:", "`bc!bewerben start`\nStartet ein Bewerbungschat", false);
                        builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                        msg.getTextChannel().sendMessage(builder.build()).queue();
                    }
                }
            } else if(args.length > 1) {
                Applier applier = null;
                for(Applier applier2 : appliers) {
                    if(applier2.member.getIdLong() == author.getIdLong()) {
                        applier = applier2;
                        break;
                    }
                }

                if(applier != null) {
                    if(msg.getTextChannel().getIdLong() == applier.textChannel) {
                        try {
                            int i = Integer.parseInt(args[0]);

                            if(applier.fields.keySet().contains(i)) {
                                String message = "";
                                for(int i2 = 1; i2 < args.length; i2++) {
                                    if((i2 + 1) == args.length) message += args[i2];
                                    else message += args[i2] + " ";
                                }
                                if(message.length() > 1024) {
                                    msg.getTextChannel().sendMessage(":x: Die Nachricht darf nicht mehr als 1024 Zeichen haben !").queue();
                                    return;
                                }
                                MessageEmbed.Field field = new MessageEmbed.Field(Applier.questions.get(i - 1), message, false, false);
                                applier.fields.replace(i, field);
                                msg.getTextChannel().sendMessage(":white_check_mark: Änderung gespeichert !").queue();
                            } else {
                                msg.getTextChannel().sendMessage(":x: `" + args[0] + "` ist keine gültige Zahl !\nOben siehst du welche Frage welche ID hat.").queue();
                            }
                        } catch (NumberFormatException e2) {
                            msg.getTextChannel().sendMessage(":x: `" + args[0] + "` ist keine gültige Zahl !").queue();
                        }
                    } else {
                        msg.getTextChannel().sendMessage(":x: Das ist kein Bewerbungschannel !").queue();
                    }
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setAuthor("Bewerbungscommand", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
                    builder.setColor(Color.blue);
                    builder.addField("Usage:", "`bc!bewerben start`\nStartet ein Bewerbungschat", false);
                    builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                    msg.getTextChannel().sendMessage(builder.build()).queue();
                }
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Bewerbungscommand", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
                builder.setColor(Color.blue);
                builder.addField("Usage:", "`bc!bewerben start`\nStartet ein Bewerbungschat", false);
                builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                msg.getTextChannel().sendMessage(builder.build()).queue();
            }
        } catch (IndexOutOfBoundsException e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Bewerbungscommand", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
            builder.setColor(Color.blue);
            builder.addField("Usage:", "`bc!bewerben start`\nStartet ein Bewerbungschat", false);
            builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
            msg.getTextChannel().sendMessage(builder.build()).queue();
        }
    }

    public boolean containsApplier(long id) {
        for(Applier applier : appliers) {
            if(applier.member.getIdLong() == id) return true;
        }
        return false;
    }

}
