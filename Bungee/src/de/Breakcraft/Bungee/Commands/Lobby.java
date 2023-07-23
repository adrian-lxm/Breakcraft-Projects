package de.Breakcraft.Bungee.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Lobby extends Command {

    public Lobby() {
        super("lobby");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            if(!(p.getServer().getInfo().getName().equals("Lobby"))) {
                p.sendMessage(TextComponent.fromLegacyText("§aDu wirst zur Lobby weiter geleitet !"));
                p.connect(ProxyServer.getInstance().getServerInfo("Lobby"));
            } else p.sendMessage(TextComponent.fromLegacyText("§cDu bist schon längst auf der Lobby !"));
        }
    }

}
