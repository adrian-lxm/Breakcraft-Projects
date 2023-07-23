package de.Breakcraft.Bungee.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Shutdown extends Command {

    public Shutdown() {
        super("stop");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender instanceof ProxiedPlayer) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Dieser Command ist nur in der Konsole verfügbar"));
            return;
        }
        ProxyServer.getInstance().stop();
    }
}
