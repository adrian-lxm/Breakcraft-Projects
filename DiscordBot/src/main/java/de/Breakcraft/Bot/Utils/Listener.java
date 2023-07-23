package de.Breakcraft.Bot.Utils;

import de.Breakcraft.Bot.BotMain;
import de.Breakcraft.Bot.Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {
    private CommandManager manager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        new ConsoleListener().start();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isFromType(ChannelType.TEXT)) {
            Message msg = event.getMessage();
            if(msg.getContentRaw().startsWith("bc!")) {
                String cmd = msg.getContentRaw().substring(3);
                for(Command command : manager.commands) {
                    if(cmd.startsWith(command.name)) {
                        command.runCommand(msg, event.getMember());
                        break;
                    }
                }
            } else {
                if(event.getChannel().getIdLong() == Long.parseLong("804789327880126513")) {
                    BotMain main = BotMain.instance;
                    if(!(main.data.levels.containsKey(event.getMember().getIdLong()))) {
                        main.data.levels.put(event.getMember().getIdLong(), 0);
                        main.data.messages.put(event.getMember().getIdLong(), 1);
                    } else {
                        int nextLevel;
                        if(main.data.levels.get(event.getMember().getIdLong()) != 0) {
                            nextLevel = 10 * (5 * main.data.levels.get(event.getMember().getIdLong()));
                        } else nextLevel = 10;
                        main.data.messages.replace(event.getMember().getIdLong(), main.data.messages.get(event.getMember().getIdLong()) + 1);
                        if(main.data.messages.get(event.getMember().getIdLong()) >= nextLevel) {
                            Member member = event.getMember();
                            main.data.levels.replace(member.getIdLong(), main.data.levels.get(member.getIdLong()) + 1);
                            TextChannel levelUp = event.getGuild().getTextChannelById("809381489623302214");
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setAuthor("Member leveled", msg.getGuild().getIconUrl(), msg.getGuild().getIconUrl());
                            builder.setColor(Color.BLUE);
                            builder.setDescription(member.getAsMention() + " ist auf Level `" + main.data.levels.get(event.getMember().getIdLong()) + "` aufgestiegen !");
                            nextLevel = 10 * (5 * main.data.levels.get(event.getMember().getIdLong()));
                            builder.addField("Nachrichten für nächsten Aufstieg", "`" + nextLevel + "` Nachrichten", false);
                            builder.setFooter("Breakcraft Bot by Shiru", msg.getJDA().getSelfUser().getAvatarUrl());
                            levelUp.sendMessage(builder.build()).queue();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        TextChannel welcomeChannel = event.getGuild().getTextChannelById("804789545563717644");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ein neuer Spieler ist beigetreten");
        TextChannel regeln = event.getGuild().getTextChannelById("804821795273834516");
        String value = "Schön, dass du da bist " + event.getMember().getAsMention() + " !\n" +
                       "Lies dir bevor du anfängst doch bitte alle " + regeln.getAsMention() + " durch!";
        embedBuilder.setDescription(value);
        embedBuilder.setFooter("Breakcraft Bot by Shiru", event.getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.setThumbnail(event.getMember().getUser().getAvatarUrl());
        embedBuilder.setAuthor("Neues Mitglied !", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
        welcomeChannel.sendMessage(embedBuilder.build()).queue();
        for(long id : BotMain.instance.data.autoroles) {
            Role autorole = event.getGuild().getRoleById(id);
            event.getGuild().addRoleToMember(event.getMember(), autorole).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        TextChannel serverlog = event.getGuild().getTextChannelById("804835639873896458");
        event.getGuild().retrieveAuditLogs().queueAfter(1, TimeUnit.SECONDS, (Logs) -> {
            boolean kicked = false, banned = false;
            User mod = null;
            String reason = null;
            for(AuditLogEntry log : Logs) {
                if (log.getTargetIdLong() == event.getUser().getIdLong()) {
                    banned = log.getType() == ActionType.BAN;
                    kicked = log.getType() == ActionType.KICK;
                    mod = log.getUser();
                    reason = log.getReason();
                    break;
                }
            }
            EmbedBuilder builder = new EmbedBuilder();
            if(banned) {
                builder.setAuthor("Member banned", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                builder.setColor(Color.RED);
                builder.setThumbnail(event.getUser().getAvatarUrl());
                builder.addField("Member", event.getUser().getAsMention(), true);
                builder.addField("Mod", mod.getAsMention(), true);
                if(reason != null) builder.addField("Grund:", reason, false);
                builder.setFooter("Breakcraft Bot by Shiru", event.getJDA().getSelfUser().getAvatarUrl());
            } else if(kicked) {
                builder.setAuthor("Member kicked", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                builder.setColor(Color.YELLOW);
                builder.setThumbnail(event.getUser().getAvatarUrl());
                builder.addField("Member", event.getUser().getAsMention(), true);
                builder.addField("Mod", mod.getAsMention(), true);
                if(reason != null) builder.addField("Grund:", reason, false);
                builder.setFooter("Breakcraft Bot by Shiru", event.getJDA().getSelfUser().getAvatarUrl());
            } else {
                builder.setAuthor("Member leaved", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                builder.setColor(Color.BLUE);
                builder.setThumbnail(event.getUser().getAvatarUrl());
                builder.addField("Member", event.getUser().getAsMention(), true);
                builder.setFooter("Breakcraft Bot by Shiru", event.getJDA().getSelfUser().getAvatarUrl());
            }
            serverlog.sendMessage(builder.build()).queue();
        });
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(event.getMember().getUser().isBot()) return;
        if(!(event.getReactionEmote().getEmoji().equals("\u2705"))) return;
        if(!(event.getGuild().getCategoryById("820317270366355496").getChannels().contains(event.getChannel()))) return;
        for(Contract contract : BotMain.contracts) {
            if(contract.message == event.getMessageIdLong()) {
                contract.handleGuildReactionEvent(event);
                return;
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.getChannel().getType() == ChannelType.PRIVATE) {
            if(event.getUser().isBot()) return;
            if(!(event.getReactionEmote().getEmoji().equals("\u2705"))) return;
            for(Contract contract : BotMain.contracts) {
                if(contract.message == event.getMessageIdLong()) {
                    contract.handleReactionEvent(event);
                    return;
                }
            }
        }
    }

}
