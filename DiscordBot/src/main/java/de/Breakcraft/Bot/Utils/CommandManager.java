package de.Breakcraft.Bot.Utils;

import de.Breakcraft.Bot.Commands.*;
import de.Breakcraft.Bot.Commands.Autorole;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    public List<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new Autorole());
        commands.add(new ServerInfo());
        commands.add(new Level());
        commands.add(new Verify());
        commands.add(new Bewerben());
        commands.add(new News());
    }

    public Command getCommandByClass(Class<? extends Command> type) {
        Command command = null;
        for(Command command2 : commands) {
            if(command2.getClass() == type) {
                command = command2;
                break;
            }
        }
        return command;
    }

}
