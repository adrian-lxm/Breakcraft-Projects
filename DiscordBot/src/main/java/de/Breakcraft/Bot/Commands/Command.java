package de.Breakcraft.Bot.Commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public abstract class Command {
    public String name;

    public abstract void runCommand(Message msg, Member author);

}
